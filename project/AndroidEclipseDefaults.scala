import sbt._
import Keys._

import android.Keys._
import android.Tasks._
// Import Eclipse plugin
import com.typesafe.sbteclipse.plugin.EclipsePlugin._
import DefaultTransforms.{ Append, Prepend }
import EclipseKeys._
import EclipseClasspathEntry.{ Src, Con, Lib }

// Import Scala XML
import scala.xml.{Node,Elem,UnprefixedAttribute,Text,Null}
import scala.xml.transform.RewriteRule
import com.typesafe.sbteclipse.core.Validation

// Some settings for sbteclipse with sbt-android-plugin and
// AndroidProguardScala.
object AndroidEclipseDefaults {

  // Import AndroidEclipse helpers
  // This fixes the output directory.
  //
  // SBTEclipse adds a target output directory to each classpath entry inside
  // the .classpath file, but this doesn't play nice with the Android plugin.
  //
  // This Transformer will :
  //   * Remove output="..." from each classpathentry
  //   * Set output="defaultOutput" to the default entry
  case class ClasspathOutputFixer(
    defaultOutput: String // Default output directory
    ) extends Function1[Seq[Node], Seq[Node]] {

    // We need Scalaz
    import scalaz.Scalaz._

    def apply(nodes: Seq[Node]): Seq[Node] = nodes.map {
      // Change the output to defaultOutput
      case node @ Elem(_, "classpathentry", _, _, _*) if (node \ "@kind" text) == "output" =>
        (node.asInstanceOf[Elem] %
          new UnprefixedAttribute("path", Text(defaultOutput), Null))

      // Remove other output dirs
      case node @ Elem(pf, "classpathentry", attrs, scope, children @ _*) =>
        Elem(pf, "classpathentry", attrs remove "output", scope, children: _*)

      case other => other
    }
  }

  // This excludes a list of files from a matching classpath entry
  case class ClasspathExclusion(
    kind: String, // Kind of classpath entry
    path: File,
    exclude: Seq[String]) extends Function1[Seq[Node], Seq[Node]] {

    // We need Scalaz
    import scalaz.Scalaz._

    // Get node attributes
    def nodePath(node: Node) = new File((node \ "@path").text)
    def nodeKind(node: Node) = (node \ "@kind").text

    def apply(nodes: Seq[Node]): Seq[Node] = nodes.map {
      // Find classpath entries with selected kind and path
      case node @ Elem(_, "classpathentry", _, _, _*) if nodeKind(node) == kind &&
        nodePath(node).getCanonicalPath == path.getCanonicalPath => {

        // Append existing excludes
        val all_excl = (exclude :+ (node \ "@excluding").text).filter(_ != "")
        val attr = new UnprefixedAttribute("excluding", Text(all_excl.mkString("|")), Null)

        // Set the new attribute
        (node.asInstanceOf[Elem] % attr)
      }

      case other => other
    }
  }
  // Output settings that play well with Eclipse and ADT :
  //   * Add managed sources to the Eclipse classpath
  //   * Fix Eclipse output
  //   * Put the resources, manifest and assets to the root dir
  //   * Generate typed resources prior to any Eclipse project
  lazy val outputSettings = Seq(
    // We want managed sources in addition to the default settings
    createSrc :=
      EclipseCreateSrc.Default +
      EclipseCreateSrc.Managed,

    // Environment to Java 1.6 (1.7 not supported by Android at the moment)
    executionEnvironment := Some(EclipseExecutionEnvironment.JavaSE16),

    // Initialize Eclipse Output to None (output will default to bin/classes)
    eclipseOutput := None,

    managedSourceDirectories in Compile := Seq(file("gen")),

    // Fix output directories
    classpathTransformerFactories <+= (eclipseOutput) {
      d => transformNode("classpath", d match {
        case Some(s) => ClasspathOutputFixer(s)
        case None => ClasspathOutputFixer("bin/classes")
      })
    },


    preTasks := Seq(typedResourcesGenerator in Android)
  )

  case class Nature(name: String)
  def nature2Xml(n: Nature): Node = <nature>{ n.name }</nature>

  case class Builder(name: String)
  implicit def builder2Xml(b: Builder): Node =
    <buildCommand>
      <name>{ b.name }</name>
      <arguments></arguments>
    </buildCommand>

  lazy val naturesSettings = Seq(
    // Set some options inside the project
    projectTransformerFactories ++= Seq(
      // Add Android and AndroidProguardScala natures
      transformNode("natures", Append(
        nature2Xml(Nature("com.android.ide.eclipse.adt.AndroidNature")),
        nature2Xml(Nature("com.restphone.androidproguardscala.Nature"))
      )),

      // Add resource builder before everything else
      transformNode("buildSpec", Prepend(
        Builder("com.android.ide.eclipse.adt.ResourceManagerBuilder")
      )),

      // Add proguard, pre-compiler and apk builder after everything else
      transformNode("buildSpec", Append(
        Builder("com.restphone.androidproguardscala.Builder"),
        Builder("com.android.ide.eclipse.adt.PreCompilerBuilder"),
        Builder("com.android.ide.eclipse.adt.ApkBuilder")
      ))
    ),

    // Add the Android lib/ folder to the classpath
    classpathTransformerFactories ++= Seq(
      transformNode("classpath", Append(
        Con("com.android.ide.eclipse.adt.LIBRARIES"),
        Src("src/main/res", None),
        Src("target/android-gen", None),
        Lib("/Users/moe/.ivy2/cache/org.scala-lang/scala-library/jars/scala-library-2.10.3.jar")
      )),
      transformNode("classpath", ClasspathExclusion(
          "src", file("src/main"), Seq("java/","scala/", "res/")
      )),
      transformNode("classpath", ClasspathExclusion(
          "src", file("src/instrumentTest"), Seq("java/","scala/", "res/")
      ))
    ),
    // Remove R.java from the managed sources
    //  (clashes with the Eclipse-generated R.java)
    classpathTransformerFactories <++=
      (managedSourceDirectories in Compile) {
        m => m map {
          path => transformNode("classpath", ClasspathExclusion(
            "src", path, Seq("**/R.java", "**/BuildConfig.java")
          ))
        }
      }
  )

  // Set default settings
  lazy val settings = Seq (
    classpathTransformerFactories := Seq(),
    projectTransformerFactories := Seq()
  ) ++ outputSettings ++ naturesSettings

}
