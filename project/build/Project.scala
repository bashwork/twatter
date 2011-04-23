import sbt._
import de.johoop.findbugs4sbt._
import FindBugsReportType._

class Project(info: ProjectInfo) extends DefaultProject(info)
    with FindBugs {

    override lazy val findbugsReportType = FancyHtml
    override lazy val findbugsReportName = "twatter-bugs.html"
}
