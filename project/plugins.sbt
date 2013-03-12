libraryDependencies <+= sbtVersion(v => "com.github.siasia" %% "xsbt-web-plugin" % "0.12.0-0.2.11.1")

resolvers ++= Seq(
  "Scala Tools" at "https://oss.sonatype.org/content/groups/scala-tools",
  "spray repo" at "http://repo.spray.cc"
)

resolvers += Resolver.url("sbt-plugin-snapshots",
  new URL("http://scalasbt.artifactoryonline.com/scalasbt/sbt-plugin-snapshots/"))(
    Resolver.ivyStylePatterns)

addSbtPlugin("com.bowlingx" %% "xsbt-wro4j-plugin" % "0.1.0-SNAPSHOT")

addSbtPlugin("com.eed3si9n" % "sbt-appengine" % "0.4.1")

addSbtPlugin("com.typesafe.sbt" % "sbt-scalariform" % "1.0.0")

addSbtPlugin("io.spray" % "sbt-twirl" % "0.6.1")
