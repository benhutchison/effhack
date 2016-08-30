lazy val root = project.in(file(".")).aggregate(effhackJS, effhackJVM).
  settings(
    publishArtifact := false,
    crossScalaVersions := Seq("2.11.8"),
    releasePublishArtifactsAction := PgpKeys.publishSigned.value,
    sonatypeProfileName := "com.github.benhutchison",
    {
      import ReleaseTransformations._
      releaseProcess := Seq[ReleaseStep](
        checkSnapshotDependencies,
        inquireVersions,
        runClean,
        runTest,
        setReleaseVersion,
        commitReleaseVersion,
        tagRelease,
        ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
        setNextVersion,
        commitNextVersion
    )}
  )

lazy val effhackCross = crossProject.in(file(".")).
  settings(
    name := "effhack",
    organization := "com.github.benhutchison",
    version := "0.1",
    scalaVersion := "2.11.8",
    libraryDependencies ++= Seq(
      "org.typelevel" %%% "cats" % "0.6.1",
      "org.scalatest" %%% "scalatest" % "3.0.0-M15" %  "test"
    ),
    libraryDependencies += "org.atnos" %% "eff-cats" % "2.0.0-RC7",

    // to write types like Reader[String, ?]
    addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.8.0"),

    // to get types like Reader[String, ?] (with more than one type parameter) correctly inferred
    addCompilerPlugin("com.milessabin" % "si2712fix-plugin_2.11.8" % "1.2.0"),
    publishTo <<= version { (v: String) =>
      Some("releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2")
    },
    publishMavenStyle := true,
    pomExtra :=
      <url>https://github.com/benhutchison/effhack</url>
      <scm>
        <url>git://github.com/benhutchison/effhack.git</url>
      </scm>
      <developers>
        <developer>
          <id>benhutchison</id>
          <name>benhutchison</name>
          <url>https://github.com/benhutchison</url>
        </developer>
      </developers>,
    licenses += ("Apache-2.0", url("http://opensource.org/licenses/Apache-2.0"))
  )

lazy val effhackJVM = effhackCross.jvm
lazy val effhackJS = effhackCross.js
