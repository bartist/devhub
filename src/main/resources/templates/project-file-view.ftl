[#import "macros.ftl" as macros]
[@macros.renderHeader i18n.translate("section.projects") /]
[@macros.renderMenu i18n user /]
		<div class="container">
[@macros.renderCommitHeader i18n group commit "List files" /]
			<div class="diff box">
				<div class="header">
					<button class="pull-right btn btn-sm btn-default folder"><i class="glyphicon glyphicon-chevron-up"></i> Fold</button>
					<button class="pull-right btn btn-sm btn-default unfolder" style="display: none;"><i class="glyphicon glyphicon-chevron-down"></i> Unfold</button>
					<h5>[@macros.renderTreeBreadcrumb group commit repository path /]</h5>
				</div>
			[#if contents?? && contents?has_content]
				<div class="scrollable">
					<table class="table diffs">
						<tbody>
				[#list contents as line]
							<tr>
								<td class="ln">${line_index + 1}</td>
								<td class="code"><pre>${line}</pre></td>
							</tr>
				[/#list]
						</tbody>
					</table>
				</div>
			[/#if]
			</div>
		</div>
[@macros.renderScripts /]
[#if highlight]
	<script src="/static/js/highlight.pack.js"></script>
[/#if]
	<script>
		$(document).ready(function() {
[#if highlight]
			hljs.configure({
				tabReplace: '&nbsp;&nbsp;&nbsp;&nbsp;',
				useBR: true
			});
			
			$('.code').each(function(i, e) {
				hljs.highlightBlock(e);
			});
[/#if]
			$(".diff").each(function() {
				var diffBody = $(this).find(".diffs");
				if (diffBody.length == 0) {
					var folder = $(this).find(".folder");
					folder.css("display", "none");
				}
			});
			
			$(".folder").click(function(e) {
				var body = $(this).parentsUntil(".box").parent();
				var unfolder = $(this).parent().find(".unfolder");
				
				body.addClass("folded");
				$(this).css("display", "none").blur();
				unfolder.css("display", "block"); 
			});
			
			$(".unfolder").click(function(e) {
				var body = $(this).parentsUntil(".box").parent();
				var folder = $(this).parent().find(".folder");

				body.removeClass("folded");
				$(this).css("display", "none").blur();
				folder.css("display", "block"); 
			});
		});
	</script>
[@macros.renderFooter /]
