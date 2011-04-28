import sbt._
import de.johoop.findbugs4sbt._
import FindBugsReportType._

class Project(info: ProjectInfo) extends DefaultProject(info)
    with FindBugs {

    /**
     * Findbugs configuration
     */
    override lazy val findbugsReportType = FancyHtml
    override lazy val findbugsReportName = "twatter-bugs.html"

    /**
     * Manifest cofiguration
     */
    override def mainClass = Some("org.twatter.main.Twatter")

    /**
     * POM cofiguration
     */
    override def pomExtra =
        <licenses>
            <license>
                <name>Apache 2</name>
                <url>http://www.apache.org/licenses/LICENSE-2.0.txt</url>
                <distribution>repo</distribution>
            </license>
        </licenses>

    /**
     * Maven cofiguration
     */

    /**
     * Dependency cofiguration
     */
    val commonsCli      = "commons-cli" % "commons-cli" % "1.2"
    val slf4jApi        = "org.slf4j" % "slf4j-api" % "1.6.1"
    val slf4jSimple     = "org.slf4j" % "slf4j-simple" % "1.6.1"
    val twitter4jStream = "org.twitter4j" % "twitter4j-stream" % "[2.1,)"
    val jedis           = "redis.clients" % "jedis" % "1.5.2"
    val luceneCore      = "org.apache.lucene" % "lucene-core" % "3.1.0"
}
