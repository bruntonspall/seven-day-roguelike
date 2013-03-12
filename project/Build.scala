import sbt._
import Keys._
import sbtappengine.Plugin.appengineSettings
import com.typesafe.sbt.SbtScalariform.scalariformSettings
import twirl.sbt.TwirlPlugin._
import com.bowlingx.sbt.plugins.Wro4jPlugin._
import Wro4jKeys._

object sevenDayRoguelikeBuild  extends Build {

  lazy val basicSettings = seq(
    version               := "1.0.0",
    organization          := "uk.co.bruntonspall.sevenday",
    description           := "",
    scalaVersion          := "2.9.1",
    scalacOptions         := Seq("-deprecation", "-encoding", "utf8")
  )


  // configure prompt to show current project
  override lazy val settings = super.settings ++ basicSettings :+ {
    shellPrompt := { s => Project.extract(s).currentProject.id + " > " }
  }

  lazy val root = Project(id="sevenDayRoguelike", base=file("."))
    .settings(
      libraryDependencies ++= Seq(
        "org.scalatra" %% "scalatra" % "2.0.4",
        "org.skife.com.typesafe.config" % "typesafe-config" % "0.3.0",
        "cc.spray" %%  "spray-json" % "1.1.1",
        "commons-codec" %  "commons-codec" % "1.6",
        "ch.qos.logback" % "logback-classic" % "0.9.26",
        "com.weiglewilczek.slf4s" %% "slf4s" % "1.0.7",
        "javax.persistence" % "persistence-api" % "1.0",
        "com.google.appengine" % "appengine-api-1.0-sdk" % "1.6.5",
        "org.scalatest" %% "scalatest" % "1.6.1" % "test",
        "javax.servlet" % "servlet-api" % "2.3" % "provided",
        "org.mortbay.jetty" % "jetty" % "6.1.22" % "container")
    )
    .settings(appengineSettings: _*)
    .settings(scalariformSettings: _*)
    .settings(Twirl.settings: _*)
    .settings(wro4jSettings: _*)
    .settings(
      Twirl.twirlImports := Seq("uk.co.bruntonspall.sevenday.model._")
    )
}
