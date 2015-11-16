package ch.mibex.bamboo.branchterminator

import com.atlassian.bamboo.deletion.DeletionService
import com.atlassian.bamboo.plan.PlanKey
import com.atlassian.bamboo.plan.cache.CachedPlanManager
import com.atlassian.bamboo.plan.cache.index.PlanRepositoryIndex
import com.atlassian.event.api.EventListener
import com.atlassian.stash.plugin.remote.event.StashBranchDeletedRemoteEvent
import org.apache.log4j.Logger

import scala.collection.JavaConverters._

class PlanBranchTerminator(deletionService: DeletionService, cachedPlanManager: CachedPlanManager) {
  private lazy val log = Logger.getLogger(getClass)

  @EventListener
  def onBranchChangedEvent(event: StashBranchDeletedRemoteEvent): Unit = {
    log.debug(s"BRANCHTERMINATOR: branch ${event.getBranchName} / ${event.getRepositorySlug} got deleted.")
    deleteMatchingPlanBranches(event)
  }

  private def deleteMatchingPlanBranches(event: StashBranchDeletedRemoteEvent) {
    findMatchingPlans(event) match {
      case Nil =>
      case matchingChains =>
        val planKeys = matchingChains.map(_.getPlanKey.toString)
        log.info(s"BRANCHTERMINATOR: will delete plan branches with keys ${planKeys.mkString(", ")}")
        deletionService.deletePlans(planKeys.asJava)
    }
  }

  private def findMatchingPlans(event: StashBranchDeletedRemoteEvent) =
    cachedPlanManager
      .getPlansWithRepository(buildPlanLookupQuery(event))
      .asScala
      .filter(ic => endsWithNumber(ic.getPlanKey))
      .toList

  private def endsWithNumber(planKey: PlanKey) =
    // plan branches always end with a number => this is an additional security check
    planKey.toString.matches("""^.+?\d$""")

  private def buildPlanLookupQuery(event: StashBranchDeletedRemoteEvent) = {
    val queryWithBranchCondition = new PlanRepositoryIndex.Query()
    queryWithBranchCondition.applicationLinkId = event.getSourceId
    queryWithBranchCondition.repositoryNamespace = event.getRepositoryProject
    queryWithBranchCondition.repositorySlug = event.getRepositorySlug
    queryWithBranchCondition.branch = event.getBranchName
    queryWithBranchCondition
  }

}
