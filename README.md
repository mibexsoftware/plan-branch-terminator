# Plan Branch Terminator for Bamboo #

[![Build Status](http://img.shields.io/travis/mibexsoftware/plan-branch-terminator.svg?style=flat-square)](https://travis-ci.org/mibexsoftware/plan-branch-terminator)
[![Latest Version](http://img.shields.io/github/release/mibexsoftware/plan-branch-terminator.svg?style=flat-square)](https://github.com/mibexsoftware/plan-branch-terminator/releases)
[![License](http://img.shields.io/badge/license-APACHE2-blue.svg?style=flat-square)](https://www.apache.org/licenses/LICENSE-2.0.html)

Deletes a plan branch instantly in [Atlassian Bamboo][bamboo] when the corresponding branch is deleted in 
[Stash / Bitbucket Server][bitbucketserver]

![][productlogo]

*No plan branch is left behind!*

*Hasta la vista, plan branch!*


## Download ##

Get it from the [releases][releases] tab and download the `plan-branch-terminator-x.x.x.jar` file.


## Motivation ##

While Bamboo allows the automatic deletion of plan branches since version 5.9 (see feature request [BAM-13129][BAM-13129]), 
this does not happen instantly when the source branch of a repository a plan branch targets is deleted. 
The deletion of the plan branch including its artifacts, logs and other data is done during a clean-up procedure
for which the interval can be configured in the plan branch settings in Bamboo:

![https://jira.atlassian.com/browse/BAM-13129][planbranch-deletion]

Until the deletion, the plan branches will remain in Bamboo as inactive plan branches:

![inactive-planbranch]

While not deleting the plan branches immediately is by design (you still might want to have access to the produced
artifacts, logs, etc. for a certain period), there are customers that make heavy use of branches (e.g., by adopting
git-flow) that want to have these plan branches being deleted instantly. Also, there is no possibility to 
configure the plan branch deletion settings globally, so you have to configure this for every plan of your Bamboo
instance separately.

This is where our little Bamboo plug-in comes in: without any configuration it will delete plan branches instantly when 
it detects that a referenced branch got deleted in Stash / Bitbucket Server. And this will also work for users having
a Bamboo version older than 5.9.


## Word of caution ##

Deleting a plan branch will irrevocably **delete all configurations, artifacts, logs and results** related to this plan
branch in Bamboo! Please also note that - even though we have tested this plug-in properly - **its use is at your own risk**.
We are not responsible for any damage or data loss incurred with its use. So please use this in one of your testing
environments first before adapting it on your production instance.


## Author

![https://www.mibexsoftware.com][mibexlogo]


[productlogo]: https://raw.githubusercontent.com/mibexsoftware/plan-branch-terminator/master/src/main/resources/images/plugin-logo.png
[planbranch-deletion]: https://raw.githubusercontent.com/mibexsoftware/plan-branch-terminator/master/src/main/resources/images/plan-branch-deletion.png
[inactive-planbranch]: https://raw.githubusercontent.com/mibexsoftware/plan-branch-terminator/master/src/main/resources/images/inactive-plan-branch.png
[BAM-13129]: https://jira.atlassian.com/browse/BAM-13129
[bamboo]: http://www.atlassian.com/bamboo
[bitbucketserver]: https://www.atlassian.com/software/bitbucket/server
[releases]: https://github.com/mibexsoftware/plan-branch-terminator/releases
[mibexlogo]: https://raw.githubusercontent.com/mibexsoftware/plan-branch-terminator/master/src/main/resources/images/vendor-logo.png
[mit]: http://opensource.org/licenses/MIT
[gh-releases]: https://github.com/mibexsoftware/plan-branch-terminator/releases
