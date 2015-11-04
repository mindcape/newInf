'use strict';

var inferneonApp = angular.module('inferneonApp', ['ngRoute', 'ui.bootstrap','editableTableWidgets', 'frontendServices', 'commonServices','spring-security-csrf-token-interceptor'])

inferneonApp.filter('excludeDeleted', function () {
    return function (input) {
        return _.filter(input, function (item) {
            return item.deleted == undefined || !item.deleted;
        });
    }
})	
	// configure our routes
	inferneonApp.config(function($routeProvider) {
		$routeProvider

			// route for the home page
			.when('/', {
				templateUrl : '/resources/pages/home.html',
				controller  : 'InferneonCtrl'
			})

			
	});



inferneonApp.controller('ProjectController', [ '$scope','$compile','$http', '$uibModalInstance', 'ProjectService', 'MessageService', '$rootScope', 
                                          function ($scope, $compile, $http, $uibModalInstance, ProjectService, MessageService, $rootScope, InferneonCtrl ) {
	
	 $scope.vm = {
	            maxNoOfProjectsPerDay: 2000,
	            currentPage: 1,
	            totalPages: 0,
	            originalProjects: [],
	            projects: [],
	            isSelectionEmpty: true,
	            errorMessages: [],
	            infoMessages: [],
	        	projectsList: []
	        };
	
	 $scope.vm.projects = {
		 projectName: '',
		 attributes: []
	 };
	$scope.addNumeric = function() {
		 var div = $("<div id= 'div"+id+"'/>");
		 
	        div.html(GetDynamicTextBox(""));
	        var temp = $compile(div)($scope);
	        angular.element(document.getElementById('TextBoxContainer')).append(temp);
	        //$("#TextBoxContainer").append(div);
	}
	
	$scope.addNominal = function() {
	   var div = $("<div id= 'div"+id+"'/>");
       div.html(GetDynamicButton(""));
       var temp = $compile(div)($scope);
       angular.element(document.getElementById('TextBoxContainer')).append(temp);
 	}
	
	var id=0;
	function GetDynamicTextBox(value) {
		id++;
		$scope.vm.projects.attributes.push({'attKey': id, 'attName':'', 'attType':'att'})
	    return '<input ng-model = "vm.projects.attName'+id+'" type="text" />&nbsp;' +
	    '<input type="button" value="x" class="remove" ng-click="removeDynamicRow(this)"/>'
	}
	function GetDynamicButton(value) {
		id++;
		$scope.vm.projects.attributes.push({'attKey': id, 'attName':'','attValues':'', 'attType':'attNValues'})
	    return  '<input ng-model = "vm.projects.attName'+id+'" type="text" />&nbsp;' + '<input ng-model = "vm.projects.attValues'+id+'" type="text" />&nbsp;' +
	    '<input type="button" value="x" class="remove" ng-click="removeDynamicRow(this)" />'
	}
	
	$scope.removeDynamicRow = function($event) {
		$(event.target).parent().remove();
	}
	
	
	  $scope.saveProject = function() {
		  console.log('Saving Data');
		  console.log($scope.projectName);
		  
		  var postData = {
				  projectName: $scope.vm.projects.projectName,
				  attributes: $scope.vm.projects.attributes
	            };
		  ProjectService.saveProjects(postData).then(function (data) {
			  console.log("Changes saved successfully"+ JSON.stringify(data));
			 $scope.vm.projectsListData =  data;
			 $uibModalInstance.close();
			 window.location.reload();
          },
          function (errorMessage) {
        	  MessageService.showErrorMessage(errorMessage);
          });
	  }
	  
	  $scope.cancelProject = function(){
		  $uibModalInstance.dismiss('cancel');
		  console.log('Cancel creation of Project');
       }
}]);


	
inferneonApp.controller('InferneonCtrl', ['$scope' ,'$http', '$rootScope', 'ProjectService', 'UserService', 'MessageService','$timeout', '$uibModal',
        function ($scope, $http, $rootScope, ProjectService, UserService, MessageService, $timeout, $uibModal) {
	
	    $scope.vm = {
	            maxNoOfProjectsPerDay: 2000,
	            currentPage: 1,
	            totalPages: 0,
	            originalProjects: [],
	            projects: [],
	            isSelectionEmpty: true,
	            errorMessages: [],
	            infoMessages: [],
	        	projectsListData: []
	        };
	    updateUserInfo();
	    loadProjectsList();
		
	    
	
			/** Model New Project */
		    $scope.openContactForm = function() {
		    	var modalInstance = $uibModal.open({
		            templateUrl: '/resources/pages/newProject.html',
		            controller: 'ProjectController',
		            backdrop: false,
		           
		        });
		    	modalInstance.result.then(function() {
		        	console.log('Clicked on Save');
		        }, function() {
		            console.log('Clicked on Cancel');
		        });
		    };
		    
            function loadProjectsList() {
            	ProjectService.searchProjects(1).then(function(data){
            		$scope.vm.errorMessages = [];
            		$scope.vm.projectsListData = data;//_.cloneDeep($scope.vm.originalProjects);
                    markAppAsInitialized();
                    if ($scope.vm.projectsListData && $scope.vm.projectsListData.length == 0) {
                        showInfoMessage("No results found.");
                    }
            	},
            	 function (errorMessage) {
                	MessageService.showErrorMessage(errorMessage);
                    markAppAsInitialized();
            		
                });
            	
            }


           /* function showErrorMessage(errorMessage) {
                clearMessages();
                $scope.vm.errorMessages.push({description: errorMessage});
            }*/

            function updateUserInfo() {
                UserService.getUserInfo()
                    .then(function (userInfo) {
                        $scope.vm.userName = userInfo.userName;
                        $scope.vm.maxNoOfProjectsPerDay = userInfo.maxNoOfProjectsPerDay;
                    },
                    function (errorMessage) {
                    	MessageService.showErrorMessage(errorMessage);
                    });
            }

            function markAppAsInitialized() {
                if ($scope.vm.appReady == undefined) {
                    $scope.vm.appReady = true;
                }
            }

            

           function clearMessages() {
                $scope.vm.errorMessages = [];
                $scope.vm.infoMessages = [];
                $scope.vm.projectsListData = [];
            }

            function updateNoOfProjectsCounterStatus() {
                var isNoOfProjectsOK = $scope.vm.todaysNoOfProjects == 'None' || ($scope.vm.todaysNoOfProjects <= $scope.vm.maxNoOfProjectsPerDay);
                $scope.vm.noOfProjectsStatusStyle = isNoOfProjectsOK ? 'cal-limit-ok' : 'cal-limit-nok';
            }

         
            $scope.updateMaxNoOfProjectsPerDay = function () {
                $timeout(function () {

                    if ($scope.vm.maxNoOfProjectsPerDay < 0) {
                        return;
                    }

                    UserService.updateMaxNoOfProjectsPerDay($scope.vm.maxNoOfProjectsPerDay)
                        .then(function () {
                        },
                        function (errorMessage) {
                        	MessageService.showErrorMessage(errorMessage);
                        });
                    updateNoOfProjectsCounterStatus();
                });
            };

            $scope.selectionChanged = function () {
                $scope.vm.isSelectionEmpty = !_.any($scope.vm.projects, function (project) {
                    return project.selected && !project.deleted;
                });
            };

            $scope.pages = function () {
                return _.range(1, $scope.vm.totalPages + 1);
            };

            $scope.search = function (page) {

                var fromDate = new Date($scope.vm.fromDate);
                var toDate = new Date($scope.vm.toDate);

                console.log('search from ' + $scope.vm.fromDate + ' ' + $scope.vm.fromTime + ' to ' + $scope.vm.toDate + ' ' + $scope.vm.toTime);

                var errorsFound = false;

                if (!errorsFound) {
                    loadProjectData(page == undefined ? 1 : page);
                }

            };

            $scope.previous = function () {
                if ($scope.vm.currentPage > 1) {
                    $scope.vm.currentPage-= 1;
                    loadProjectData($scope.vm.currentPage);
                }
            };

            $scope.next = function () {
                if ($scope.vm.currentPage < $scope.vm.totalPages) {
                    $scope.vm.currentPage += 1;
                    loadProjectData($scope.vm.currentPage);
                }
            };

            $scope.goToPage = function (pageNumber) {
                if (pageNumber > 0 && pageNumber <= $scope.vm.totalPages) {
                    $scope.vm.currentPage = pageNumber;
                    loadProjectData(pageNumber);
                }
            };

            $scope.add = function () {
                $scope.vm.projects.unshift({
                	selected: false,
                    new: true
                });
            };

            $scope.delete = function () {
                var deletedProjectIds = _.chain($scope.vm.projects)
                    .filter(function (project) {
                        return project.selected && !project.new;
                    })
                    .map(function (project) {
                        return project.id;
                    })
                    .value();

                ProjectService.deleteProjects(deletedProjectIds)
                    .then(function () {
                    	MessageService.clearMessages();
                        showInfoMessage("deletion successful.");

                        _.remove($scope.vm.projects, function(project) {
                            return project.selected;
                        });

                        $scope.selectionChanged();
                        updateUserInfo();

                    },
                    function () {
                    	MessageService.clearMessages();
                        $scope.vm.errorMessages.push({description: "deletion failed."});
                    });
            };

            $scope.reset = function () {
                $scope.vm.projects = $scope.vm.originalProjects;
            };

            function getNotNew(projects) {
                return  _.chain(projects)
                    .filter(function (project) {
                        return !project.new;
                    })
                    .value();
            }

            function prepareProjectsDto(projects) {
                return  _.chain(projects)
                    .map(function (project) {
                        return {
                            id: project.id,
                            projectName: project.projectName,
                            createdTS: project.createdTS                           
                        }
                    })
                    .value();
            }

            $scope.save = function () {

                var maybeDirty = prepareProjectsDto(getNotNew($scope.vm.projects));

                var original = prepareProjectsDto(getNotNew($scope.vm.originalProjects));

                var dirty = _.filter(maybeDirty).filter(function (project) {

                    var originalProject = _.filter(original, function (orig) {
                        return orig.id === project.id;
                    });

                    if (originalProject.length == 1) {
                        originalProject = originalProject[0];
                    }

                    return originalProject && ( originalProject.createdTS != project.createdTS ||
                        originalProject.projectName != project.projectName ||
                        originalProject.noOfProjects != project.noOfProjects)
                });

                var newItems = _.filter($scope.vm.projects, function (project) {
                    return project.new;
                });

                var saveAll = prepareProjectsDto(newItems);
                saveAll = saveAll.concat(dirty);

                $scope.vm.errorMessages = [];

                // save all new items plus the ones that where modified
                ProjectService.saveProjects(saveAll).then(function () {
                        $scope.search($scope.vm.currentPage);
                        showInfoMessage("Changes saved successfully");
                        updateUserInfo();
                    },
                    function (errorMessage) {
                    	MessageService.showErrorMessage(errorMessage);
                    });

            };

            $scope.logout = function () {
                UserService.logout();
            }


        }]);
    
