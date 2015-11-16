package ch.mibex.bamboo.branchterminator

import com.atlassian.bamboo.deletion.DeletionService
import com.atlassian.bamboo.plan.PlanKeys
import com.atlassian.bamboo.plan.cache.index.PlanRepositoryIndex
import com.atlassian.bamboo.plan.cache.{CachedPlanManager, ImmutableChain}
import com.atlassian.stash.plugin.remote.event.StashBranchDeletedRemoteEvent
import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import org.specs2.specification.Scope

import scala.collection.JavaConverters._

@RunWith(classOf[JUnitRunner])
class PlanBranchTerminatorTest extends Specification with Mockito {

  class AuthContext extends Scope {
    val deletionService = mock[DeletionService]
    val cachedPlanManager = mock[CachedPlanManager]
    val branchDeletionEvent = mock[StashBranchDeletedRemoteEvent]
    val planBranchTerminator = new PlanBranchTerminator(deletionService, cachedPlanManager)
  }

  "plan branch terminator" should {

    "not delete any plan branches when no matching ones were found" in new AuthContext {
      cachedPlanManager.getPlansWithRepository(any[PlanRepositoryIndex.Query]) returns List[ImmutableChain]().asJava

      planBranchTerminator.onBranchChangedEvent(branchDeletionEvent)

      there was no(deletionService).deletePlans(any)
    }

    "delete matching plan branch" in new AuthContext {
      val planBranch = mock[ImmutableChain]
      planBranch.getPlanKey returns PlanKeys.getPlanKey("TEST-TWOM22")
      cachedPlanManager.getPlansWithRepository(any[PlanRepositoryIndex.Query]) returns List(planBranch).asJava

      planBranchTerminator.onBranchChangedEvent(branchDeletionEvent)

      there was one(deletionService).deletePlans(List("TEST-TWOM22").asJava)
    }

    "not delete found plan branches not ending with a number in their key" in new AuthContext {
      val planBranch = mock[ImmutableChain]
      planBranch.getPlanKey returns PlanKeys.getPlanKey("TEST-TWOM")
      cachedPlanManager.getPlansWithRepository(any[PlanRepositoryIndex.Query]) returns List(planBranch).asJava

      planBranchTerminator.onBranchChangedEvent(branchDeletionEvent)

      there was no(deletionService).deletePlans(any)
    }

    "delete all plan branches referencing deleted branch" in new AuthContext {
      val planBranch1 = mock[ImmutableChain]
      planBranch1.getPlanKey returns PlanKeys.getPlanKey("TEST-ONEM11")
      val planBranch2 = mock[ImmutableChain]
      planBranch2.getPlanKey returns PlanKeys.getPlanKey("TEST-TWOM22")
      cachedPlanManager.getPlansWithRepository(any[PlanRepositoryIndex.Query]) returns List(planBranch1, planBranch2).asJava

      planBranchTerminator.onBranchChangedEvent(branchDeletionEvent)

      there was one(deletionService).deletePlans(List("TEST-ONEM11", "TEST-TWOM22").asJava)
    }

  }

}