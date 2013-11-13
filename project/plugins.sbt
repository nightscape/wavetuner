addSbtPlugin("com.hanhuy.sbt" % "android-sdk-plugin" % "1.2.4")

addSbtPlugin("com.typesafe.sbteclipse" % "sbteclipse-plugin" % "2.5.0-SNAPSHOT")

//lazy val root = project.in( file(".") ).dependsOn( eclipsePlugin )
//lazy val eclipsePlugin = uri("git://github.com/nightscape/sbteclipse#transformer_convenience")

resolvers += "sonatype-releases" at "https://oss.sonatype.org/content/repositories/releases/"
