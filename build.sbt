Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3 = project.in(file("."))
  .settings(
    scalaVersion := "3.3.1",
    scalacOptions ++= Seq(
      "-Ysafe-init",      // make forward reference a warning
      "-Xfatal-warnings"  // make all waring as fatal
    ),
    libraryDependencies ++= Seq(
      "co.fs2"            %% "fs2-io"          % "3.9.2",
      "org.scalatest"     %% "scalatest"       % "3.2.16",
      "org.scalacheck"    %% "scalacheck"      % "1.17.0",
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.16.0",
      "com.lihaoyi"       %% "pprint"          % "0.8.1",
    )
  )
