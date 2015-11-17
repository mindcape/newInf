angular.module('commonServices', [])
	.service('MessageService', ['$http', '$q', '$rootScope', '$timeout', function($http, $q, $rootScope, $timeout) {
		$rootScope.vm = {
		            errorMessages: [],
		            infoMessages: [],
		        	projectsList: []
		        };
		
		 return {
			 /**
			  * Clear the messages Global level
			  */
			 clearMessages: function () {
				 $rootScope.vm.errorMessages = [];
				 $rootScope.vm.infoMessages = [];
				 $rootScope.vm.rootProjectsList = [];
	            },
		 /**
		  * Show info messages
		  */
		 showInfoMessage : function (infoMessage) {
			 $rootScope.vm.infoMessages = [];
			 $rootScope.vm.infoMessages.push({description: infoMessage});
             $timeout(function () {
            	 $rootScope.vm.infoMessages = [];
             }, 1000);
         },
         /**
          * Show Error message
          */
         showErrorMessage: function (errorMessage) {
        	 this.clearMessages();
             $rootScope.vm.errorMessages.push({description: errorMessage});
         }
         
         
         
        
		 }
	 }])
