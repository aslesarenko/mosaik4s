val scala213 = "2.13.8"
val scala3Version = "3.1.2"

resolvers += "jitpack" at "https://jitpack.io"

lazy val mosaik4s = project
  .in(file("."))
  .settings(
    name := "mosaik4s",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies ++= Seq(
      "com.github.MrStahlfelge" % "mosaik" % "0.1.0",
      "org.jetbrains.kotlin" % "kotlin-stdlib" % "1.6.10",
      "org.scalatest" %% "scalatest" % "3.2.11" % Test
    )
  )

