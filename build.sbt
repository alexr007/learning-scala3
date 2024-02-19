Global / onChangedBuildSource := ReloadOnSourceChanges

lazy val scala3 = project.in(file("."))
  .settings(
    scalaVersion := "3.3.2", // 3.4.0, 3.2.2
    scalacOptions ++= Seq(
      "-Ysafe-init",     // make forward reference a warning
      "-Xfatal-warnings" // make all waring as fatal
    ),
    libraryDependencies ++= Seq(
      "co.fs2"            %% "fs2-io"          % "3.9.4",
      "org.scalatest"     %% "scalatest"       % "3.2.18",
      "org.scalacheck"    %% "scalacheck"      % "1.17.0",
      "org.scalatestplus" %% "scalacheck-1-17" % "3.2.18.0",
      "com.lihaoyi"       %% "pprint"          % "0.8.1",
    )
  )
