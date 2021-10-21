= Deploy to clojars

== Create a pom.xml (or use the one from Github)

~~~
clj -Spom
~~~

Author the `pom.xml` file with:

1. proper `groupId` and `version`.
2. Add clojars 

~~~xml
<distributionManagement>
  <repository>
    <id>clojars</id>
    <name>Clojars repository</name>
    <url>https://clojars.org/repo</url>
  </repository>
</distributionManagement>
~~~

== Credentials

Make sure `~/.m2/settings.xml` contain your Clojars credentials

~~~xml
<settings>
  <servers>
    <server>
      <id>clojars</id>
      <username>username</username>
      <password>deploy-token</password>
    </server>
  </servers>
</settings>
~~~

== Deploy

~~~
mvn deploy
~~~


