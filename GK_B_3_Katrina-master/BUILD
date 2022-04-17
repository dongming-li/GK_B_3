java_binary(
    name = "Backend",
    srcs = glob(["src/main/java/katrina/server/*.java"]),
)

java_binary(
    name = "StudentScraper",
    srcs = glob(["src/main/java/katrina/studentscraper/*.java"]),
    deps = ["@org_jsoup_jsoup//jar"],
)

load("@io_bazel_rules_gwt//gwt:gwt.bzl", "gwt_application")
gwt_application(
    name = "Frontend",
    srcs = glob(["src/main/java/katrina/client/*.java"]),
    modules = ["katrina.client.Launcher"],
    pubs = glob(["src/main/java/katrina/client/public/*"], exclude_directories = 0),
    resources = glob(["src/main/java/katrina/client/*.gwt.xml"])
)
