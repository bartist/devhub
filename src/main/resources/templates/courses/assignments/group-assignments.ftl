[#import "../../macros.ftl" as macros]
[#import "../../components/project-frameset.ftl" as projectFrameset]
[@macros.renderHeader i18n.translate("section.projects") /]
[@macros.renderMenu i18n user /]
<div class="container">

    [@projectFrameset.renderBreadcrumb i18n group/]

    <div class="row">
        <div class="col-md-10 col-md-offset-2">
            <h4 style="line-height:34px; margin-top:0;">Assignments</h4>
        </div>
    </div>

    <div class="row">
        <div class="col-md-2">
            [@projectFrameset.renderSidemenu "assignments" i18n group repository/]
        </div>
        <div class="col-md-10">
        [#assign assignments = course.getAssignments()]
            <table class="table table-bordered">
                <colgroup>
                    <col span="1" width="5%"/>
                    <col span="1" width="65%"/>
                    <col span="1" width="10%"/>
                    <col span="1" width="10%"/>
                    <col span="1" width="10%"/>
                </colgroup>
            [#if assignments?? && assignments?has_content]
                <thead>
                <tr>
                    <th>#</th>
                    <th>${i18n.translate("course.control.assignment")}</th>
                    <th>${i18n.translate("course.control.due-date")}</th>
                    <th>${i18n.translate("delivery.grade")}</th>
                    <th>${i18n.translate("delivery.status")}</th>
                </tr>
                </thead>
            [/#if]
                <tbody>
                [#if assignments?? && assignments?has_content]
                    [#list assignments as assignment]
                    [#assign delivery = deliveries.getLastDelivery(assignment, group)!]
                    <tr>
                        <td>
                            <a href="/courses/${course.getCode()}/groups/${group.getGroupNumber()}/assignments/${assignment.getAssignmentId()}">
                            ${assignment.getAssignmentId()!"-"}
                            </a>
                        </td>
                        <td>
                            <a href="/courses/${course.getCode()}/groups/${group.getGroupNumber()}/assignments/${assignment.getAssignmentId()}">
                            ${assignment.getName()!"-"}
                            </a>
                        </td>
                        <td>
                            [#if assignment.getDueDate()??]
                                <a href="/courses/${course.getCode()}/groups/${group.getGroupNumber()}/assignments/${assignment.getAssignmentId()}">
                                ${assignment.getDueDate()}
                                </a>
                            [/#if]
                        </td>
                        <td>
                            [#if delivery?has_content && delivery.getReview()??]
                            [#assign review = delivery.getReview()]
                            ${review.getGrade()!"-"}
                            [/#if]
                        </td>
                        <td>
                            [#if delivery?has_content]
                                [#assign state = delivery.getState()]
                                <a class="label label-${state.style}" href="/courses/${course.getCode()}/groups/${group.getGroupNumber()}/assignments/${assignment.getAssignmentId()}">
                                    ${i18n.translate(state.translationKey)}
                                </a>
                            [#else]
                                <a class="label label-default" href="/courses/${course.getCode()}/groups/${group.getGroupNumber()}/assignments/${assignment.getAssignmentId()}">
                                    ${i18n.translate("assignment.not-submitted")}
                                </a>
                            [/#if]
                        </td>
                    </tr>
                    [/#list]
                [#else]
                <tr>
                    <td class="muted" colspan="5">${i18n.translate("course.no-assignments")}</td>
                </tr>
                [/#if]
                </tbody>
            </table>
        </div>
    </div>
</div>
[@macros.renderScripts /]
[@macros.renderFooter /]