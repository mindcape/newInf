//angular.module('ui.bootstrap.demo', ['ngAnimate', 'ui.bootstrap']);
angular.module('inferneonApp').controller('ModalDemoCtrl', function ($scope, $uibModal, $log) {

  $scope.items = ['item1', 'item2', 'item3'];

  $scope.animationsEnabled = true;

  $scope.open = function (size) {

    var modalInstance = $uibModal.open({
      animation: $scope.animationsEnabled,
      templateUrl: 'myModalContent.html',
      controller: 'ModalInstanceCtrl',
      size: size,
      resolve: {
        items: function () {
          return $scope.items;
        }
      }
    });

    modalInstance.result.then(function (selectedItem) {
      $scope.selected = selectedItem;
    }, function () {
      $log.info('Modal dismissed at: ' + new Date());
    });
  };

  $scope.toggleAnimation = function () {
    $scope.animationsEnabled = !$scope.animationsEnabled;
  };

});

// Please note that $modalInstance represents a modal window (instance) dependency.
// It is not the same as the $uibModal service used above.

angular.module('inferneonApp').controller('ModalInstanceCtrl', function ($scope, $uibModalInstance, items) {

  $scope.items = items;
  $scope.selected = {
    item: $scope.items[0]
  };

  $scope.ok = function () {
    $uibModalInstance.close($scope.selected.item);
  };

  $scope.cancel = function () {
    $uibModalInstance.dismiss('cancel');
  };
});




/*'use strict';

define(['inferneonApp'], function (inferneonApp) {
	
	var projectControler = function ($scope) {
		 $(function () {
	     	    $("#btnAdd").bind("click", function () {
	     	        var div = $("<div />");
	     	        div.html(GetDynamicTextBox(""));
	     	        $("#TextBoxContainer").append(div);
	     	    });
	     	    
	     	    
	     	    $("#btn2Add").bind("click", function () {
	     	        var div = $("<div />");
	     	        div.html(GetDynamicButton(""));
	     	        $("#TextBoxContainer").append(div);
	     	    });
	     	    $("#btnGet").bind("click", function () {
	     	        var values = "";
	     	        $("input[name=DynamicTextBox]").each(function () {
	     	            values += $(this).val() + "\n";
	     	        });
	     	        alert(values);
	     	    });
	     	   $("body").on("click", ".remove", function () {
	     	        $(this).closest("div").remove();
	     	    });
	     	});
	     	var id=0;
	     	function GetDynamicTextBox(value) {
	     		id++;
	     	    return '<input name = "numeric'+id+'" type="text" value = "' + value + '" />&nbsp;' +
	     	    '<input type="button" value="x" class="remove" />'
	     	}
	     	var i=0;
	     	function GetDynamicButton(value) {
	     		i++;
	     	    return  '<input name = "nominal'+i+'" type="text" value = "' + value + '" />&nbsp;' + '<input name = "attribute_value'+i+'" type="text" value = "' + value + '" />&nbsp;' +
	     	    '<input type="button" value="x" class="remove" />'
	     	}
	}
	
	inferneonApp.register.controller('projectControler', projectControler);
});*/