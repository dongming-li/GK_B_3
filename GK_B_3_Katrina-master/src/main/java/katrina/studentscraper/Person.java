package katrina.studentscraper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Person implements Comparable<Person>, Serializable {
    final String name;
    final String url;

    Map<String, String> attributes;

    public Person(String name, String url, Map<String, String> attributes) {
        this.name = name;
        this.url = url;
        this.attributes = attributes;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(name).append("\t");
        attributes.forEach((k, v) -> sb.append('<').append(k).append(':').append(v).append("> "));
        return sb.toString();
    }

    @Override
    public int compareTo(Person o) {
        return url.compareToIgnoreCase(o.url);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Person && compareTo((Person) obj) == 0;
    }
}
