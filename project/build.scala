import sbt._
import Keys._
import sbtassembly.AssemblyPlugin.autoImport._

object build extends Build {

  val akkaActor = "com.typesafe.akka" %% "akka-actor" % "2.4.1"
  val httpClient = "org.apache.httpcomponents" % "httpclient" % "4.5.1"

  val argonaut = "io.argonaut" %% "argonaut" % "6.0.4"

  //  val specs = "org.scala-tools.testing" %% "specs" % "1.6.9"
  val commonsIo = "commons-io" % "commons-io" % "2.3"
  val commonsLang = "org.apache.commons" % "commons-lang3" % "3.1"

  val scalaCompiler = "org.scala-lang" % "scala-compiler" % "2.11.7"
  val jetty = "org.eclipse.jetty.aggregate" % "jetty-webapp" % "7.6.2.v20120308" intransitive
  val jackson = "org.codehaus.jackson" % "jackson-jaxrs" % "1.9.2"
  val jersey = "com.sun.jersey" % "jersey-bundle" % "1.12"
  val servletApi = "javax.servlet" % "servlet-api" % "2.5"
  val jgit = "org.eclipse.jgit" % "org.eclipse.jgit" % "1.3.0.201202151440-r"
  val jgitServer = "org.eclipse.jgit" % "org.eclipse.jgit.http.server" % "1.3.0.201202151440-r"
  val scalaTest = "org.scalatest" %% "scalatest" % "2.2.6" % "test"
  val testNg = "org.testng" % "testng" % "6.5.1" % "test"
  val specs2 = "org.specs2" %% "specs2" % "3.7" % "test"
  //  val specs2Scalaz = "org.specs2" %% "specs2-scalaz-core" % "7.0.0"
  val specs2Scalaz = "org.typelevel" %% "scalaz-specs2" % "0.3.0" % "test"


  def project(id: String, base: String = null) = Project(id = id, base = file(Option(base).getOrElse(id))).settings(
    Seq(
      version := Option(System.getProperty("version")).getOrElse("dev-SNAPSHOT"),
      organization := "uk.co.hackthetower",
      Keys.scalaVersion := "2.11.7",
      scalaBinaryVersion := "2.11",
      resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
      fork in Test := false,
      parallelExecution in Test := true,
      scalaSource in Compile <<= baseDirectory / "src",
      scalaSource in Test <<= baseDirectory / "test",
      packageOptions <<= version map {
        scalatronVersion => Seq(Package.ManifestAttributes(
          ("Implementation-Version", scalatronVersion)
        ))
      },
      assemblyMergeStrategy in assembly := {
        case "plugin.properties" => MergeStrategy.first
        case "about.html" => MergeStrategy.first
        case x =>
          val oldStrategy = (assemblyMergeStrategy in assembly).value
          oldStrategy(x)
      }
    ): _*)


  lazy val core = project("ScalatronCore")
    .settings(Seq(
      libraryDependencies ++= Seq(
        akkaActor
      ),
      // , logLevel in assembly := Level.Debug
      assemblyJarName in assembly := "ScalatronCore.jar"
    ))

  lazy val cli = project("ScalatronCLI")
    .settings(Seq(
      libraryDependencies ++= Seq(
        httpClient,
        argonaut
      ),
      assemblyJarName in assembly := "ScalatronCLI.jar"
    ))

  lazy val botwar = project("BotWar")
    .dependsOn(core)
    .settings(Seq(
      libraryDependencies ++= Seq(
        akkaActor
      ),
      // , logLevel in assembly := Level.Debug
      assemblyJarName in assembly := "BotWar.jar"
    )
    )

  lazy val markdown = project("ScalaMarkdown")
    .settings(Seq(
      scalaSource in Compile <<= baseDirectory / "src",
      scalaSource in Test <<= baseDirectory / "test/scala",
      resourceDirectory in Test <<= baseDirectory / "test/resources",
      libraryDependencies ++= Seq(
        specs2,
        commonsIo,
        commonsLang
      ),
      assemblyJarName in assembly := "ScalaMarkdown.jar"
    ))


  lazy val main = project("Scalatron")
    .dependsOn(botwar)
    .settings(Seq(
      libraryDependencies ++= Seq(
        akkaActor,
        scalaCompiler,
        jetty,
        jackson,
        jersey,
        servletApi,
        jgit,
        jgitServer,
        scalaTest,
        testNg,
        specs2,
        specs2Scalaz
      ),
      resolvers += "JGit Repository" at "http://download.eclipse.org/jgit/maven",
      // , logLevel in assembly := Level.Debug
      assemblyJarName in assembly := "Scalatron.jar"
    )
    )

  lazy val all = project("all", ".")
    .settings(Seq(distTask))
    .aggregate(main, cli, markdown, referenceBot, tagTeamBot, debugFileBot, debugStatusBot)


  lazy val samples = IO.listFiles(file("Scalatron") / "samples") filter (!_.isFile) map {
    sample: File => sample.getName -> project(sample.getName.filter(_.isLetterOrDigit), sample.getName)
      .settings(Seq(
        scalaSource in Compile <<= baseDirectory / ("../Scalatron/samples/%s/src" format sample.getName),
        artifactName in packageBin := ((_, _, _) => "ScalatronBot.jar")
      ))
  } toMap

  // TODO How can we do this automatically?!?
  lazy val referenceBot = samples("Example Bot 01 - Reference")
  lazy val tagTeamBot = samples("Example Bot 02 - TagTeam")
  lazy val debugFileBot = samples("Example Bot 03 - Debug File Logger")
  lazy val debugStatusBot = samples("Example Bot 04 - Debug Status Logger")

  val dist = TaskKey[Unit]("dist", "Makes the distribution zip file")
  val distTask = dist <<= (version, scalaBinaryVersion) map { (scalatronVersion, version) =>
    println("Beginning distribution generation...")
    val distDir = file("dist")

    // clean distribution directory
    println("Deleting /dist directory...")
    IO delete distDir

    // create new distribution directory
    println("Creating /dist directory...")
    IO createDirectory distDir
    val scalatronDir = file("Scalatron")

    println("Copying Readme.txt and License.txt...")
    for (fileToCopy <- List("Readme.txt", "License.txt")) {
      IO.copyFile(scalatronDir / fileToCopy, distDir / fileToCopy)
    }

    for (dirToCopy <- List("webui", "doc/pdf")) {
      println("Copying " + dirToCopy)
      IO.copyDirectory(scalatronDir / dirToCopy, distDir / dirToCopy)
    }

    val distSamples = distDir / "samples"
    def sampleJar(sample: Project) = sample.base / ("target/scala-%s/ScalatronBot.jar" format version)
    for (sample <- samples.values) {
      if (sampleJar(sample).exists) {
        println("Copying " + sample.base)
        IO.copyDirectory(sample.base / ("../Scalatron/samples/%s/src" format sample.base.getName), distSamples / sample.base.getName / "src")
        IO.copyFile(sampleJar(sample), distSamples / sample.base.getName / "ScalatronBot.jar")
      }
    }

    println("Copying Reference bot to /bots directory...")
    IO.copyFile(sampleJar(referenceBot), distDir / "bots" / "Reference" / "ScalatronBot.jar")


    def markdown(docDir: File, htmlDir: File) = {
      Seq("java", "-Xmx1G", "-jar", ("ScalaMarkdown/target/scala-%s/ScalaMarkdown.jar" format version), docDir.getPath, htmlDir.getPath) !
    }

    // generate HTML from Markdown, for /doc and /devdoc
    println("Generating /dist/doc/html from /doc/markdown...")
    markdown(scalatronDir / "doc/markdown", distDir / "doc/html")

    println("Generating /webui/tutorial from /dev/tutorial...")
    markdown(scalatronDir / "doc/tutorial", distDir / "webui/tutorial")


    // Copy service jars
    for (jar <- List("Scalatron", "ScalatronCLI", "ScalatronCore", "BotWar")) {
      IO.copyFile(file(jar) / ("target/scala-%s/" format version) / (jar + ".jar"), distDir / "bin" / (jar + ".jar"))
    }

    // This is ridiculous, there has to be be an easier way to zip up a directory
    val zipFileName = "scalatron-%s.zip" format scalatronVersion
    println("Zipping up /dist into " + zipFileName + "...")
    def zip(srcDir: File, destFile: File, prepend: String) = {
      val allDistFiles = (srcDir ** "*").get.filter(_.isFile).map { f => (f, prepend + IO.relativize(distDir, f).get) }
      IO.zip(allDistFiles, destFile)
    }
    zip(distDir, file("./" + zipFileName), "Scalatron/")
  } dependsOn(
    assembly in core,
    assembly in botwar,
    assembly in main,
    assembly in cli,
    assembly in markdown,
    packageBin in Compile in referenceBot,
    packageBin in Compile in tagTeamBot,
    packageBin in Compile in debugFileBot,
    packageBin in Compile in debugStatusBot)
}