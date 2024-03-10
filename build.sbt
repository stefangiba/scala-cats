val scala3Version = "3.3.3"
val catsVersion   = "2.10.0"

lazy val cats = project
  .in(file("."))
  .settings(
    name         := "scala-cats",
    version      := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-Wunused:all"),
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-core" % catsVersion
    ),
    libraryDependencies ++= Seq(
      "org.scalameta" %% "munit" % "0.7.29" % Test
    )
  )
