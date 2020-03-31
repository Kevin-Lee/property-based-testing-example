import ProjectInfo._
import kevinlee.sbt.SbtCommon.crossVersionProps
import just.semver.SemVer
import SemVer.{Major, Minor}

val ProjectScalaVersion: String = "2.13.1"

lazy val hedgehogVersion = "97854199ef795a5dfba15478fd9abe66035ddea2"
lazy val hedgehogRepo: MavenRepository =
  "bintray-scala-hedgehog" at "https://dl.bintray.com/hedgehogqa/scala-hedgehog"

lazy val hedgehogLibs: Seq[ModuleID] = Seq(
    "qa.hedgehog" %% "hedgehog-core" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-runner" % hedgehogVersion % Test
  , "qa.hedgehog" %% "hedgehog-sbt" % hedgehogVersion % Test
)

lazy val libCatsCore: Seq[ModuleID] = Seq("org.typelevel" %% "cats-core" % "2.1.1")
lazy val libCatsEffect: Seq[ModuleID] = Seq("org.typelevel" %% "cats-effect" % "2.1.2")

lazy val libCatsCore_2_0_0: Seq[ModuleID] = Seq("org.typelevel" %% "cats-core" % "2.0.0")
lazy val libCatsEffect_2_0_0: Seq[ModuleID] = Seq("org.typelevel" %% "cats-effect" % "2.0.0")

ThisBuild / scalaVersion     := ProjectScalaVersion
ThisBuild / version          := ProjectVersion
ThisBuild / organization     := "io.kevinlee"
ThisBuild / organizationName := "Kevin's Code"
ThisBuild / developers   := List(
  Developer("Kevin-Lee", "Kevin Lee", "kevin.code@kevinlee.io", url("https://github.com/Kevin-Lee"))
)

lazy val root = (project in file("."))
  .settings(
      name := "property-based-testing-example"
    , description  := "Property-based testing example"
    , resolvers ++= Seq(
      Resolver.sonatypeRepo("releases")
      , hedgehogRepo
    )
    , addCompilerPlugin("org.typelevel" % "kind-projector" % "0.11.0" cross CrossVersion.full)
    , addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    , libraryDependencies :=
      crossVersionProps(
          hedgehogLibs
        , SemVer.parseUnsafe(scalaVersion.value)
      ) {
          case x =>
            libraryDependencies.value ++ libCatsCore ++ libCatsEffect
        }
    /* Ammonite-REPL { */
    , libraryDependencies ++=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          Seq.empty[ModuleID]
        case "2.11" =>
          Seq("com.lihaoyi" % "ammonite" % "1.6.7" % Test cross CrossVersion.full)
        case "2.12" =>
          Seq.empty[ModuleID] // TODO: add ammonite when it supports Scala 2.12.11
        case _ =>
          Seq("com.lihaoyi" % "ammonite" % "2.0.4" % Test cross CrossVersion.full)
      })
    , sourceGenerators in Test +=
      (scalaBinaryVersion.value match {
        case "2.10" =>
          task(Seq.empty[File])
        case "2.12" =>
          task(Seq.empty[File]) // TODO: add ammonite when it supports Scala 2.12.11
        case _ =>
          task {
            val file = (sourceManaged in Test).value / "amm.scala"
            IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
            Seq(file)
          }
      })
    /* } Ammonite-REPL */
    , wartremoverErrors in (Compile, compile) ++= commonWarts((scalaBinaryVersion in update).value)
    , wartremoverErrors in (Test, compile) ++= commonWarts((scalaBinaryVersion in update).value)
    , testFrameworks ++= Seq(TestFramework("hedgehog.sbt.Framework"))
    , licenses += ("MIT", url("http://opensource.org/licenses/MIT"))

    , initialCommands in console :=
      """"""
  )

