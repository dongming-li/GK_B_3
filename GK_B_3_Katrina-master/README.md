Katrina
===

Semantic analysis of Iowa State social media

## Build
Built using the [Bazel](https://www.bazel.build/) build tool.

`bazel build :Backend` creates a JAR and executable binary for the server project

`bazel build :Backend_deploy.jar` creates a standalone jar including the project's maven dependencies in `bazel-bin/Launcher_deploy.jar`

`bazel run :Backend` runs the server project (and builds if outdated)

`bazel run :Frontend-dev` builds and runs a Jetty dev server

`bazel build :Frontend` builds the GWT project


# MySQL connection
Host: mysql.cs.iastate.edu
Schema: db309gkb3
User: dbu309gkb3
Pass: rtaS2wGF

For example, to use the MySQL command line client,
you would type:
  mysql -h mysql.cs.iastate.edu -u dbu309gkb3 -p db309gkb3
and type the password "rtaS2wGF" when prompted.

## MySQL Schema

MySQL [db309gkb3]> describe tweets;
+-----------------------+--------------+------+-----+---------+----------------+
| Field                 | Type         | Null | Key | Default | Extra          |
+-----------------------+--------------+------+-----+---------+----------------+
| id                    | int(11)      | NO   | PRI | NULL    | auto_increment |
| text                  | varchar(561) | NO   |     | NULL    |                |
| user_id               | bigint(20)   | NO   |     | NULL    |                |
| created_at            | varchar(100) | YES  |     | NULL    |                |
| in_reply_to_status_id | bigint(20)   | YES  |     | NULL    |                |
| in_reply_to_user_id   | bigint(20)   | YES  |     | NULL    |                |
| retweet_count         | int(11)      | YES  |     | NULL    |                |
| favorite_count        | int(11)      | YES  |     | NULL    |                |
| retweeted_status_id   | bigint(20)   | YES  |     | NULL    |                |
| date_scanned          | bigint(20)   | YES  |     | NULL    |                |
+-----------------------+--------------+------+-----+---------+----------------+
10 rows in set (0.00 sec)
