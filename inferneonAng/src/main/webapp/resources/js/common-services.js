angular.module('commonServices', [])
	.service('MessageService', ['$http', '$q', '$rootScope', '$timeout', function($http, $q, $rootScope, $timeout) {
		this.projectData=[];
		$rootScope.vm = {
		            errorMessages: [],
		            infoMessages: [],
		        	projectsList: []
		        };
		 return {
			 
			 clearMessages: function () {
				 $rootScope.vm.errorMessages = [];
				 $rootScope.vm.infoMessages = [];
				 $rootScope.vm.rootProjectsList = [];
	            },
		 
		 showInfoMessage : function (infoMessage) {
			 $rootScope.vm.infoMessages = [];
			 $rootScope.vm.infoMessages.push({description: infoMessage});
             $timeout(function () {
            	 $rootScope.vm.infoMessages = [];
             }, 1000);
         },
         
         showErrorMessage: function (errorMessage) {
             clearMessages();
             $rootScope.vm.errorMessages.push({description: errorMessage});
         },
         
         assignResponseData: function (responseData) {
        	 $rootScope.vm.rootProjectsList=responseData;
         }
         

			 
		 }
	 }])
