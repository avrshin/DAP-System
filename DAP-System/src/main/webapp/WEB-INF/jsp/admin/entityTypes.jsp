<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/xml" prefix="x"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<c:set value="${pageContext.request.contextPath}" var="ctx" scope="request" />
<!DOCTYPE html>
<html ng-app="app">
<head>
<meta charset="UTF-8">
<jsp:include page="css-includes.jsp" />
<jsp:include page="js-includes.jsp">
	<jsp:param name="which" value="entityTypes" />
</jsp:include>

</head>
<body ng-controller="EntityTypesCtrl">
	<jsp:include page="admin-menu.jsp" />
	<div>
		<h3>Add entity type</h3>
		<form novalidate name="addEntityTypeForm" class="css-form">
			<table class="table table-bordered table-hover table-condensed">
				<tr style="font-weight: bold">
					<td style="width: 20%">Code</td>
					<td style="width: 60%">Default name</td>
					<td style="width: 20%">Action</td>
				</tr>
				<tr>
					<td><input type="text" class="form-control" placeholder="Code" ng-model="newentityType.code" required /></td>
					<td><input type="text" class="form-control" placeholder="Name" ng-model="newentityType.name" required /></td>
					<td style="white-space: nowrap">
						<button class="btn btn-primary btn-custom-default" ng-click="addEntityType(newentityType)">Add</button>
					</td>
				</tr>
			</table>
		</form>
	</div>
	<h3>Entity types</h3>
	<div>
		<table class="table table-bordered table-hover table-condensed">
			<tr style="font-weight: bold">
				<td style="width: 20%"><a href="" ng-click="predicate='code'; reverse=!reverse">Code</a></td>
				<td style="width: 20%"><a href="" ng-click="predicate='name'; reverse=!reverse">Default name</a></td>
				<td style="width: 40%">Translations</td>
				<td style="width: 20%">Action</td>
			</tr>
			<tr ng-repeat="entityType in entityTypes | orderBy:predicate:reverse">
				<td>
					<!-- non editable code --> <span e-name="code" e-form="rowform"> {{ entityType.code }} </span>
				</td>
				<td>
					<!-- editable name --> <span editable-text="entityType.name" e-class="form-control" e-name="name" e-form="rowform" e-required> {{ entityType.name }} </span>
				</td>
				<td>
					<table class="table table-bordered table-hover table-condensed">
						<tr style="font-weight: bold">
							<td style="width: 20%"><a href="" ng-click="t_predicate='code'; t_reverse=!t_reverse">Language</a></td>
							<td style="width: 55%"><a href="" ng-click="t_predicate='value'; t_reverse=!t_reverse">Translation</a></td>
							<td style="width: 25%">Action</td>
						</tr>
						<tr ng-repeat="translation in entityType.translations | orderBy:t_predicate:t_reverse">
							<td>
								<!-- non editable language --> <span e-class="form-control" e-name="language" e-form="t_rowform"> {{ translation.code }} </span>
							</td>
							<td>
								<!-- editable value --> <span editable-text="translation.value" e-class="form-control" e-name="value" e-form="t_rowform"> {{ translation.value }} </span>
							</td>
							<td style="white-space: nowrap">
								<!-- t form -->
								<form editable-form name="t_rowform" onbeforesave="saveTranslation($data, entityType.text_id, translation.code)" ng-show="t_rowform.$visible" class="form-buttons form-inline" shown="inserted == translation">
									<button type="submit" ng-disabled="t_rowform.$waiting" class="btn btn-primary btn-xs btn-custom-default" >Save</button>
									<button type="button" ng-disabled="t_rowform.$waiting" ng-click="t_rowform.$cancel()" class="btn btn-default btn-xs btn-custom-cancel" >Cancel</button>
								</form>
								<div class="buttons" ng-show="!t_rowform.$visible">
									<button class="btn btn-primary btn-xs btn-custom-default" ng-click="t_rowform.$show()">Edit</button>
									<button class="btn btn-danger btn-xs btn-custom-danger" ng-click="removeTranslation(entityType.text_id, translation.code)">Delete</button>
								</div>
							</td>
						</tr>
						<tr ng-show="showAddTranslation(entityType.id)">
							<td><select class="form-control" ng-model="newtranslation[$index].language" ng-options="language.code for language in languages | filter:languagesByAvailableTranslations(entityType.id, $index)" ng-class="default">
							</select></td>
							<td><input type="text" class="form-control" placeholder="Translation" ng-model="newtranslation[$index].value"/></input></td>
							<td><button class="btn btn-primary btn-xs btn-custom-default" ng-click="addTranslation(entityType.id, entityType.text_id, $index)">Add</button></td>
						</tr>
					</table>
					<div ng-show="!showAddTranslation(entityType.id)" class="translations_complete">
						<span>All translations complete. Do you wish to <a href="${ctx}/admin/misc/languages/">add another language</a> ?</span>
					</div>
				</td>
				<td style="white-space: nowrap">
					<!-- form -->
					<form editable-form name="rowform" onbeforesave="saveEntityType($data, entityType.id)" ng-show="rowform.$visible" class="form-buttons form-inline" shown="inserted == entityType">
						<button type="submit" ng-disabled="rowform.$waiting" class="btn btn-primary btn-custom-default">Save</button>
						<button type="button" ng-disabled="rowform.$waiting" ng-click="rowform.$cancel()" class="btn btn-default btn-custom-cancel">Cancel</button>
					</form>
					<div class="buttons" ng-show="!rowform.$visible">
						<button class="btn btn-primary btn-custom-default" ng-click="rowform.$show()">Edit</button>
						<button class="btn btn-danger btn-custom-danger" ng-click="removeEntityType(entityType.id)">Delete</button>
					</div>
				</td>
			</tr>
		</table>
	</div>
	<!-- h3>Test zone</h3>
	<pre>
		<p>EntityTypes : {{ entityTypes | json }}</p>
		<p>Languages : {{ languages | json }}</p>
	</pre>
	
	<h2>Add a new entity type</h2>

	<form method="POST" action="">
		<label for="code">Code</label>
		<input type="text" name="code" id="code" />
		<label for="name">Name</label>
		<input type="text" name="name" id="name" />
		<input type="submit" value="submit" />

	</form>

	<h2>List of entity types</h2>
	<table>
		<tr>
			<th>Id</th>
			<th>Code</th>
			<th>Name</th>
		</tr>

		<c:forEach var="entityType" items="${it}">
			<tr>
				<td>${entityType.id}</td>
				<td>${entityType.code}</td>
				<td>${entityType.name}</td>
			</tr>
		</c:forEach>

	</table -->

</body>
</html>