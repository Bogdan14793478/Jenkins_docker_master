package scripts

import groovy.transform.BaseScript
import jenkins.model.*;
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.nodes.JobRestrictionProperty;
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.job.RegexNameRestriction;
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.JobRestriction
import com.synopsys.arc.jenkinsci.plugins.jobrestrictions.restrictions.logic.MultipleOrJobRestriction;
import io.jenkins.plugins.jobrestrictions.restrictions.job.JobClassNameRestriction;
import io.jenkins.plugins.jobrestrictions.util.ClassSelector;
import jenkins.branch.OrganizationFolder;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.multibranch.WorkflowMultiBranchProject;
import core.JenkinsScript

@BaseScript JenkinsScript jenkinsScript

// Setup restriction for IaC job
def repoConfig = getConfig().getInfraRepo()
def systemFolder = 'Tools'
def instance = Jenkins.get();

instance.setNumExecutors(2);
instance.setLabelString("imperator");
instance.setMode(hudson.model.Node.Mode.EXCLUSIVE);

List<JobRestriction> JobRestrictions = new ArrayList<JobRestriction>()

// https://issues.jenkins-ci.org/browse/JENKINS-31866?focusedCommentId=334368&page=com.atlassian.jira.plugin.system.issuetabpanels%3Acomment-tabpanel#comment-334368
// classesRestriction is needed to grant permissions for scheduling jobs on agents
ClassSelector multibranchJob = new ClassSelector(WorkflowMultiBranchProject.class.name);
ClassSelector workflowJob = new ClassSelector(WorkflowJob.class.name);
ClassSelector bitbucketJob = new ClassSelector(OrganizationFolder.class.name);
def classesRestriction = new JobClassNameRestriction([multibranchJob, workflowJob, bitbucketJob]);
JobRestrictions.add(classesRestriction)

def jobsRestrictionFirstList = "${systemFolder}/.*"
def nameRestrictionFirst = new RegexNameRestriction(jobsRestrictionFirstList, false);
JobRestrictions.add(nameRestrictionFirst)

if (repoConfig && repoConfig.MainJobName) {
    def jobsRestrictionSecondList = "${repoConfig.MainJobName}"
    def nameRestrictionSecond = new RegexNameRestriction(jobsRestrictionSecondList, false);
    JobRestrictions.add(nameRestrictionSecond)
}

def jobRestriction = new MultipleOrJobRestriction(JobRestrictions);
def prop = new JobRestrictionProperty(jobRestriction)

if(!instance.getNodeProperty(JobRestrictionProperty)) {
    instance.nodeProperties.add(prop);
    println 'INFO: Restricted master to only allow execution from system jobs.';
} else {
    println 'INFO: Nothing changed. Master already restricts jobs.';
}

instance.save();