import android.Keys._

import com.typesafe.sbteclipse.plugin.EclipsePlugin._

import EclipseKeys._

android.Plugin.androidBuild

AndroidEclipseDefaults.settings

name := "wavetuner"

version := "1.2.2"

scalaVersion := "2.10.3"

scalacOptions ++= Seq("-P:continuations:enable")

//logLevel := Level.Debug

platformTarget in Android := "android-19"

// call install and run without having to prefix with android:
run <<= run in Android

install <<= install in Android

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-library" % "2.10.3",
  "org.scalatest" %% "scalatest" % "2.0" % "test",
  "org.scalautils" %% "scalautils" % "2.0" % "test",
  "junit" % "junit" % "4.8.2" % "test",
  "org.robolectric" % "robolectric" % "2.2" % "test"
  )

autoCompilerPlugins := true

addCompilerPlugin("org.scala-lang.plugins" % "continuations" % "2.10.3")

//useProguard in Android := false

proguardOptions in Android ++= Seq("-dontobfuscate", "-dontoptimize",
  "-dontwarn org.apache.http.**",
  "-dontwarn org.robolectric.**",
  "-dontwarn org.scalatest.**")
