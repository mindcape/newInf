'use strict';

var inferneonApp = angular.module('inferneonApp', ['ngRoute', 'ui.bootstrap','editableTableWidgets', 'frontendServices', 'commonServices','spring-security-csrf-token-interceptor'])

/**
 * Configure Routes
 */
inferneonApp.config(function($routeProvider) {
	$routeProvider

		/**
		 * Route to the home page after login this page will load in to the body of index page as part of angular router
		 */
		.when('/', {
			templateUrl : '/resources/pages/home.html',
			controller  : 'InferneonCtrl'
		}).when('/projectEdit/:projectEditId', {
			templateUrl : '/resources/pages/projectDetails.html',	
			controller : 'ProjectEditController'
			
		})
});

/**
 * Factory for passing or broadcast data among controllers
 */
inferneonApp.factory("MessageBus", function($rootScope) {
	return {
		broadcast : function(event, data) {
			$rootScope.$broadcast(event, data);
		}
	};
});

/**
 * New Project Controller
 */
inferneonApp.controller('ProjectController', [ '$scope','$compile','$http','$routeParams', '$uibModalInstance', 'ProjectService', 'MessageService', 'MessageBus','$rootScope', 'editProjectId',
                                          function ($scope, $compile, $http, $routeParams, $uibModalInstance, ProjectService, MessageService,MessageBus, $rootScope,editProjectId  ) {
		 $scope.vm.editProjectId = editProjectId;
		 if($scope.vm.editProjectId) {
			 loadEditProjectDeatils($scope.vm.editProjectId);
		 }
			$scope.vm.projects = {
				id : '',
				projectName : '',
				attributes : [],
				newAttrs:[]
			};

			$scope.addNumericAtt = function() {
				$scope.vm.projects.attributes.push({
					'attName' : '',
					'attType' : 'ATT',
					'attOrder':''
				});
			}

			$scope.addNominalAtt = function() {
				$scope.vm.projects.attributes.push({
					'attName' : '',
					'attValidValues' : '',
					'attType' : 'ATTV',
					'attOrder':''
				});
			}

			$scope.removeDynamicRow = function($event) {
				$(event.target).parent().remove();
			}

			$scope.vm.postProjects = {
				projectName : '',
				attributes : []
			}
			$scope.saveProject = function() {
				console.log('Saving Data');
				console.log($scope.projectName);
				MessageService.clearMessages();
				var postData = {
					id : $scope.vm.projects.id,
					projectName : $scope.vm.projects.projectName,
					attributes : $scope.vm.projects.attributes
				};
				ProjectService.saveProjects(postData).then(
						function(data) {
							console.log("Changes saved successfully"+ JSON.stringify(data));
							MessageBus.broadcast("dataHasCome", data);
							$uibModalInstance.close();
						}, function(errorMessage) {
							MessageService.showErrorMessage(errorMessage);
						});
			}
			/**
			 * Cancel New Project
			 */
			$scope.cancelProject = function() {
				$uibModalInstance.dismiss('cancel');
				console.log('Cancel Project Save');
			}
			
			function loadEditProjectDeatils (projectId) {
		     	ProjectService.loadProject(projectId).then(function(data){
		     		 console.log("Get the project details successfully"+ JSON.stringify(data));
		     		MessageService.clearMessages();
		     		 if (data && data.length == 0) {
			             	MessageService.showInfoMessage("No results found.");
			             } else {
			            	 $scope.vm.projects.id = data.id;
			            	 $scope.vm.projects.projectName = data.projectName;
			            	 $scope.vm.projects.attributes = data.attributes;
			             }
		     	},
		     	 function (errorMessage) {
		         	MessageService.showErrorMessage(errorMessage);
		         	MessageService.markAppAsInitialized();
		     		
		         });
		     	
			}
	 
}]);

inferneonApp.controller('ProjectEditController',['$scope' ,'$http','$location', 'ProjectService', 'MessageService','$rootScope', '$routeParams','$uibModal',  
                                                 function ($scope, $http, $location, ProjectService, MessageService, $rootScope, $routeParams, $uibModal){
			$scope.prjId = '';
			$scope.vm.projectData = [];

			if ($routeParams.projectEditId) {
				$scope.prjId = $routeParams.projectEditId;
				loadProjectDeatils($scope.prjId);
			} else {
				alert("false value");
			}

			$scope.saveEditProject = function() {
				alert("Save : " + $scope.prjId);
			}

			function loadProjectDeatils(projectId) {
				ProjectService.loadProject(projectId).then(
						function(data) {
							console.log("Get the project details successfully"
									+ JSON.stringify(data));
							MessageService.clearMessages();
							$scope.vm.projectData = data;
							$scope.vm.projects.projectName = data.projectName;
							$scope.vm.projects.attributes = data.attributes;
							
							// markAppAsInitialized();
							if ($scope.vm.projectData
									&& $scope.vm.projectData.length == 0) {
								MessageService
										.showInfoMessage("No results found.");
							}
						}, function(errorMessage) {
							MessageService.showErrorMessage(errorMessage);
							MessageService.markAppAsInitialized();

						});

			}
	 
}]);



/**
 * Main Application Controller
 */
	
inferneonApp.controller('InferneonCtrl', ['$scope' ,'$http','$location', '$rootScope', 'ProjectService', 'UserService', 'MessageService','$timeout', '$uibModal',
        function ($scope, $http,$location, $rootScope, ProjectService, UserService, MessageService, $timeout, $uibModal) {
		
			$scope.vm.data = [];
			$scope.vm = {
		            projects: [],
		            errorMessages: [],
		            infoMessages: [],
		        	projectsListData: []
		        };
			/**
			 * this $on method will receive the data from the broad cast factory
			 */
			$scope.$on("dataHasCome", function(event, data){
		        console.log(event.name);
		        console.log(data);
		        $scope.vm.data = data;
		    })
			    
		    updateUserInfo();
		    loadProjectsList();
	
			/** New Project Modal open function */
		    $scope.openNewProjectForm = function(projectId) {
		    	var modalInstance = $uibModal.open({
		            templateUrl: '/resources/pages/newProject.html',
		            controller: 'ProjectController',
		            backdrop: false,
		            resolve: {
		                editProjectId: function () {
		                  return projectId;
		                }
		              }
		        });
		    	modalInstance.result.then(function() {
		        	console.log('Clicked on Save');
		        }, function() {
		            console.log('Clicked on Cancel');
		        });
		    };
		    
		    
		    $scope.editProject = function (projectId) {
		    	$location.path('/projectEdit/'+projectId);
		    }
		    
		    /**
		     * function to load the project list 
		     */
            function loadProjectsList() {
            	ProjectService.searchProjects(1).then(function(data){
            		MessageService.clearMessages();
            		$scope.vm.data  = data;
                    markAppAsInitialized();
                    if ($scope.vm.data && $scope.vm.data.length == 0) {
                    	MessageService.showInfoMessage("No results found.");
                    }
            	},
            	 function (errorMessage) {
                	MessageService.showErrorMessage(errorMessage);
                    markAppAsInitialized();
            		
                });
            	
            }
            /**
             * Update the User information to display in home page.
             */

            function updateUserInfo() {
                UserService.getUserInfo()
                    .then(function (userInfo) {
                        $scope.vm.userName = userInfo.userName;
                    },
                    function (errorMessage) {
                    	MessageService.showErrorMessage(errorMessage);
                    });
            }
            /**
             * Application initialization method.
             */
            function markAppAsInitialized() {
                if ($scope.vm.appReady == undefined) {
                    $scope.vm.appReady = true;
                }
            }

           /**
            * Log out function
            */
           $scope.logout = function () {
               UserService.logout();
           }
        }]);
    
