import Dependencies._

name := "hc_image_ops"

organization := "com.austindata"

version := "0.1"

scalaVersion := "2.11.7"

scalacOptions ++= Seq("-optimize", "-deprecation", "-feature")

resolvers ++= Seq("Github Repo" at "http://davenatx.github.io/maven")

libraryDependencies ++= Dependencies.hcImageOps

git.baseVersion := "0.1"

//versionWithGit

showCurrentGitBranch

scalariformSettings

org.scalastyle.sbt.ScalastylePlugin.Settings