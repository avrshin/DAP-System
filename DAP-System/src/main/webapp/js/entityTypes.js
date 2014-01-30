var app = angular.module("app", [ "xeditable" ]);

app.run(function(editableOptions) {
  editableOptions.theme = 'bs2'; // Theme : can be 'bs3, 'bs2' or 'default'
});

app.controller('EntityTypesCtrl', function($scope, $filter, $http) {

  // Sort management
  $scope.predicate = 'code';
  $scope.t_predicate = 'code';

  // ////////////////////
  // EntityTypes management
  // ////////////////////

  $scope.entityTypes = [];

  // Load entityTypes
  $scope.loadEntityTypes = function() {
    return $http.get(dapContextRoot + '/admin/curated/entitytypes/json').success(function(data) {
      $scope.entityTypes = data;
      $scope.resetNewTranslations();
    });
  };

  if (!$scope.entityTypes.length) {
    $scope.loadEntityTypes();
  }

  // Save (update) an entityType
  $scope.saveEntityType = function(data, id) {
    var valid = $scope.checkUpdateForm(data);
    if ("OK" == valid) {

      return $http.post(dapContextRoot + '/admin/curated/entitytypes/submitupdate', "entityTypeId=" + id + "&newName=" + data.name, {
        headers : {
          'Content-Type' : 'application/x-www-form-urlencoded'
        }
      }).success(function(data, status, headers, config) {
        // this callback will be called asynchronously
        // when the response is available
        $scope.loadEntityTypes();
      }).error(function(data, status, headers, config) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        alert("Entity type update threw an error. No entity type has been updated.");
        $scope.loadEntityTypes();
      });
    } else {
      alert("Form not valid ! \r\n" + valid);
      return "";
    }
  };

  // - check that the updated entity type is valid
  $scope.checkUpdateForm = function(data) {
    var name = data.name;
    if ('' == name || null == name) {
      return "Name cannot be empty.";
    }
    return "OK";
  };

  // Remove an entity type
  $scope.removeEntityType = function(id) {
    if (!confirm("Do you really want to delete this entity type ?")) {
      return;
    }
    $http.post(dapContextRoot + '/admin/curated/entitytypes/submitdelete', "entityTypeId=" + id, {
      headers : {
        'Content-Type' : 'application/x-www-form-urlencoded'
      }
    }).success(function(data, status, headers, config) {
      // this callback will be called asynchronously
      // when the response is available
      $scope.loadEntityTypes();
    }).error(function(data, status, headers, config) {
      // called asynchronously if an error occurs
      // or server returns response with an error status.
      alert("Entity type deletion threw an error. Maybe this entity type is used by some entity. No entity type has been deleted.");
    });
  };

  // add entityType
  // - the new entityType
  $scope.newentityType;

  // - reset it
  $scope.resetNewEntityType = function() {
    if (!$scope.newentityType) {
      $scope.newentityType = {};
    }
    $scope.newentityType.code = "";
    $scope.newentityType.name = "";

  };

  // - reset its form
  $scope.resetAddEntityTypeForm = function() {
    $scope.addEntityTypeForm.$setPristine();
  };

  // - add it
  $scope.addEntityType = function(data) {
    var valid = $scope.checkForm(data);
    if ("OK" == valid) {
      // alert("Add entityType : " + data);
      return $http.post(dapContextRoot + '/admin/curated/entitytypes/submitadd', "code=" + data.code + "&name=" + data.name, {
        headers : {
          'Content-Type' : 'application/x-www-form-urlencoded'
        }
      }).success(function(data, status, headers, config) {
        // this callback will be called asynchronously
        // when the response is available
        // alert("EntityType added !");
        $scope.resetNewEntityType();
        $scope.resetAddEntityTypeForm();
        $scope.loadEntityTypes();
      }).error(function(data, status, headers, config) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        // alert("EntityType addition threw error : \r\n" + data);
        alert("Entity type addition threw an error. Maybe this entity type already exists. No entity type has been created.");
      });
    } else {
      alert("Form not valid ! \r\n" + valid);
    }
  };

  // - check that the new entityType is complete
  $scope.checkForm = function(data) {
    var code = data.code;
    if ('' == code || null == code) {
      return "Code cannot be empty.";
    }
    var name = data.name;
    if ('' == name || null == name) {
      return "Name cannot be empty.";
    }
    return "OK";
  };

  // Get an entity type by its id
  $scope.getEntityTypeById = function(entityTypeId) {
    var filteredEntityTypes = $filter('filter')($scope.entityTypes, {
      id : entityTypeId
    });
    var theEntityType = filteredEntityTypes && 0 < filteredEntityTypes.length ? filteredEntityTypes[0] : null;
    return theEntityType;
  }

  // ////////////////////////
  // Translations management
  // ////////////////////////

  $scope.languages = [];

  // Load languages
  $scope.loadLanguages = function() {
    return $http.get(dapContextRoot + '/admin/misc/languages/json').success(function(data) {
      $scope.languages = data;
      $scope.resetNewTranslations();
    });
  };

  // Are the languages there ?
  $scope.checkLanguages = function() {
    return $scope.languages && 0 < $scope.languages.length;
  }

  if (!$scope.checkLanguages()) {
    $scope.loadLanguages();
  }

  // Get a language by its code
  $scope.getLanguageByCode = function(languageCode) {
    var filteredLanguages = $filter('filter')($scope.languages, {
      code : languageCode
    });
    var theLanguage = filteredLanguages && 0 < filteredLanguages.length ? filteredLanguages[0] : null;
    return theLanguage;
  }

  // Get a translation value for an entity type and a language code
  $scope.getTranslationForEntityTypeAndLanguageCode = function(entityType, languageCode) {
    var filteredTranslations = $filter('filter')(entityType.translations, {
      code : languageCode
    });
    var theTranslation = filteredTranslations && 0 < filteredTranslations.length ? filteredTranslations[0] : null;
    return theTranslation ? theTranslation.value : null;
  }

  // Do we have to show the "Add translation" form ? Only if there are some translations missing
  $scope.showAddTranslation = function(entityTypeId) {

    // Are there some languages ?
    if (!$scope.checkLanguages()) {
      return false;
    }

    // Get the entityType
    var theEntityType = $scope.getEntityTypeById(entityTypeId);
    return theEntityType && theEntityType.translations && theEntityType.translations.length < $scope.languages.length;
  };

  // Show only the languages for which some translations are missing
  $scope.languagesByAvailableTranslations = function(entityTypeId, index) {
    return function(language) {
      var translation = $scope.getTranslationForEntityTypeAndLanguageCode($scope.getEntityTypeById(entityTypeId), language.code);

      // At the same time, select by default the first missing language
      if (!translation) {
        if (!$scope.newtranslation[index]) {
          $scope.newtranslation[index] = {};
          $scope.newtranslation[index].value = "";
        }
        if (!$scope.newtranslation[index].language || (!$scope.newtranslation[index].language.code)) {
          $scope.newtranslation[index].language = language;
        }
      }
      return null == translation;
    }
  };

  // add translation
  // - the new translation
  $scope.newtranslation;

  // - reset it
  $scope.resetNewTranslations = function() {
    if (!$scope.newtranslation) {
      $scope.newtranslation = [];
    }
    for (i = 0; i < $scope.entityTypes.length; i++) {
      $scope.newtranslation[i] = {};
      $scope.newtranslation[i].value = "";
    }
    $scope.resetAddTranslationForms();
  };

  // - reset its form
  $scope.resetAddTranslationForms = function() {
    for ( var theForm in $scope.t_rowform) {
      theForm.$setPristine();
    }
  };

  // - add it
  $scope.addTranslation = function(entityType_id, text_id, index) {
    var data = $scope.newtranslation[index];
    var valid = $scope.checkTranslation(data);
    if ("OK" == valid) {
      return $http.post(dapContextRoot + '/admin/translations/submitadd',
          "textId=" + text_id + "&languageCode=" + data.language.code + "&translationValue=" + data.value, {
            headers : {
              'Content-Type' : 'application/x-www-form-urlencoded'
            }
          }).success(function(data, status, headers, config) {
        // this callback will be called asynchronously
        // when the response is available
        // alert("Translation added !");
        $scope.resetNewTranslations();
        $scope.resetAddTranslationForms();

        // TODO We could improve this with : $scope.loadTranslations(entityType_id);
        $scope.loadEntityTypes();

      }).error(function(data, status, headers, config) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        // alert("EntityType addition threw error : \r\n" + data);
        alert("Translation addition threw an error. Maybe this translation already exists. No translation has been created.");
      });
    } else {
      alert("Form not valid ! \r\n" + valid);
    }
  };

  // - check that the new translation is complete
  $scope.checkTranslation = function(data) {
    var value = data.value;
    if ('' == value || null == value) {
      return "Translation cannot be empty.";
    }
    return "OK";
  };

  // Save (update) a translation
  $scope.saveTranslation = function(data, text_id, language_code) {
    // alert(data + ", " + id)
    var valid = $scope.checkUpdateTranslation(data);
    if ("OK" == valid) {

      return $http.post(dapContextRoot + '/admin/translations/submitupdate',
          "textId=" + text_id + "&languageCode=" + language_code + "&translationValue=" + data.value, {
            headers : {
              'Content-Type' : 'application/x-www-form-urlencoded'
            }
          }).success(function(data, status, headers, config) {
        // this callback will be called asynchronously
        // when the response is available

        // TODO We could improve this with : $scope.loadTranslations(entityType_id);
        $scope.loadEntityTypes();
      }).error(function(data, status, headers, config) {
        // called asynchronously if an error occurs
        // or server returns response with an error status.
        alert("Translation update threw an error. No translation has been updated.");

        // TODO We could improve this with : $scope.loadTranslations(entityType_id);
        $scope.loadEntityTypes();
      });
    } else {
      alert("Form not valid ! \r\n" + valid);
      return "";
    }
  };

  // - check that the new translation is complete
  $scope.checkUpdateTranslation = function(data) {
    var value = data.value;
    if ('' == value || null == value) {
      return "Translation cannot be empty.";
    }
    return "OK";
  };
  
  // Remove a translation
  $scope.removeTranslation = function(text_id, language_code) {
    if (!confirm("Do you really want to delete this translation ?")) {
      return;
    }

    $http.post(dapContextRoot + '/admin/translations/submitdelete', "textId=" + text_id + "&languageCode=" + language_code, {
      headers : {
        'Content-Type' : 'application/x-www-form-urlencoded'
      }
    }).success(function(data, status, headers, config) {
      // this callback will be called asynchronously
      // when the response is available
      // alert("Language deleted !");

      // TODO We could improve this with : $scope.loadTranslations(entityType_id);
      $scope.loadEntityTypes();
    }).error(function(data, status, headers, config) {
      // called asynchronously if an error occurs
      // or server returns response with an error status.
      // alert("Language " + id + " deletion threw error : \r\n" + data);
      alert("Translation deletion threw an error. No translation has been deleted.");
    });
  };
});