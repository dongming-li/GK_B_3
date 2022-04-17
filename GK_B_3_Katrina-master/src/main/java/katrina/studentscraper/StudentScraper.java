package katrina.studentscraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentScraper {

    static Set<Person> people = Collections.synchronizedSet(new TreeSet<Person>());

    static Pattern phoneRegex = Pattern.compile("(\\d+)-(\\d+)-(\\d+)$");
    static Pattern attrPattern = Pattern.compile("(.*):\\s(.*)");
    Pattern netidReg = Pattern.compile("(\\w+)");

    Document connect(String url) {
        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
            return null;
        }
    }

    Stream<String> getDepartments() {
        Document doc = connect("http://info.iastate.edu/individuals/advanced");
        Element departments = doc.getElementById("department_select");
        return departments.children().stream().map(Element::val)
                // urls must have the spaces escaped
                .map(depName -> depName.replaceAll(" ", "%20"))
                //ignore the empty "" selection
                .skip(1);
    }

    List<String> profileUrlsOnPage(String dept, int page) {
        String url = "http://info.iastate.edu/individuals/advanced?department=" + dept + "&individual_type=both&orderby=0&orderdir=0&page=" + page;
        Document doc = connect(url);

        // no users in dept
        if(!doc.getElementsByClass("wd-Alert--error").isEmpty()) return Collections.emptyList();

        Stream<String> urls = doc.getElementsByClass("dir-Listing-item").stream()
                .map(e -> e.attr("href"))
                .map(extension -> "http://info.iastate.edu" + extension);

        return urls.collect(Collectors.toList());
    }

    boolean validPhone(String p) {
        Matcher pm = phoneRegex.matcher(p);
        return pm.find();
    }

    Person scrapePersonInfo(String url) {
        Document doc = connect(url);
        String name = doc.getElementsByTag("h1").first().text();

        HashMap<String, String> attributes = new HashMap<>();

        String email = parseEmail(doc);
        if(email != null) attributes.put("Email", email);

        Elements attrElements = doc.getElementsByClass("dir-Person-item");

        Optional<String> phone = attrElements.stream().map(e -> e.child(0).text())
                .filter(this::validPhone).findFirst();
        phone.ifPresent(p -> attributes.put("Phone", p));

        Elements addressChilds = doc.getElementsContainingText("In-Session Address");
        if(!addressChilds.isEmpty()){
            String unformedAddr = addressChilds.last().parent().text();
            String addr = unformedAddr.replace("In-Session Address", "");
            if(phone.isPresent()) addr = addr.replace(phone.get(), "");
            attributes.put("Address", addr);
        }

        // adds other, unknown attributes
        attrElements.stream()
                .filter(e -> e.child(0).tagName().equalsIgnoreCase("span"))
                .map(Element::text)
                .map(s -> attrPattern.matcher(s))
                .forEach(m -> {
                    m.find();
                    attributes.put(m.group(1), m.group(2));
                });

        Person person = new Person(name, url, attributes);
        return person;
    }

    String parseEmail(Document doc) {
        if(doc.getElementsContainingOwnText("(at) iastate").size() == 0) return null;
        String jsString = doc.getElementsContainingOwnText("(at) iastate").last().text();
        Matcher netidMatcher = netidReg.matcher(jsString);
        netidMatcher.find();
        String netid = netidMatcher.group(1);
        return netid + "@iastate.edu";
    }

    void addPeopleInDpt(String dpt) {
        int duplicates = 0;
        int page = 1;

        while(duplicates < 2) {
            List<String> urlsOnPage = profileUrlsOnPage(dpt, page);
            for(String url : urlsOnPage) {
                Person p = scrapePersonInfo(url);
                boolean isUnique = people.add(p);
                if(!isUnique) ++duplicates;
            }
            ++page;
        }
    }

    public static void main(String[] args) throws IOException {

        StudentScraper sc = new StudentScraper();

        sc.getDepartments().parallel().forEach(sc::addPeopleInDpt);

        Path hashSetDat = Paths.get(System.getProperty("user.home") + "/hashset.dat");
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(hashSetDat.toFile()));
        oos.writeObject(people);
        oos.close();

    }

}
