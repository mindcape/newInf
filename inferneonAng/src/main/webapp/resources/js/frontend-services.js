angular.module('frontendServices', ['commonServices'])
    .service('ProjectService', ['$http', '$q',  'MessageService', function($http, $q, MessageService) {
        return {
            searchProjects: function(pageNumber) {
                var deferred = $q.defer();
                $http.get('/project/',{
                    params: {
                        pageNumber: pageNumber
                    }
                })
                .then(function (response) {
                    if (response.status == 200) {
                        deferred.resolve(response.data);
                    }
                    else {
                        deferred.reject('Error retrieving list of projects');
                    }
                });

                return deferred.promise;
            },

            deleteProjects: function(deletedProjectIds) {
                var deferred = $q.defer();

                $http({
                    method: 'DELETE',
                    url: '/project',
                    data: deletedProjectIds,
                    headers: {
                        "Content-Type": "application/json"
                    }
                })
                .then(function (response) {
                    if (response.status == 200) {
                        deferred.resolve();
                    }
                    else {
                        deferred.reject('Error deleting projects');
                    }
                });

                return deferred.promise;
            },
            
            saveProjects: function(postData) {
              var deferred = $q.defer();
      		  console.log('Saving Data');
      		
      		  
      		  $http({
                    method: 'POST',
                    url: '/project',
                    data: postData,
                    headers: {
                        "Content-Type": "application/json"                  
                    }
                }).then(function (response) {
              	  	console.log('reponse : '+response.data);
                    if (response.status == 200) {
                    	 deferred.resolve(response.data);
                    	 MessageService.assignResponseData(response.data);
                    
                    } else {
                    	 deferred.reject("Error saving projects: " + response.data);
                    }
                });
                
      		return deferred.promise;
      		 
      	  }

        }
    }])
    .service('UserService', ['$http','$q', function($http, $q) {
        return {
            getUserInfo: function() {
                var deferred = $q.defer();

                $http.get('/user')
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve(response.data);
                        }
                        else {
                            deferred.reject('Error retrieving user info');
                        }
                });

                return deferred.promise;
            },
            updateMaxNoOfProjectsPerDay: function(maxNoOfProjectsPerDay) {
                var deferred = $q.defer();

                $http.put('/user', maxNoOfProjectsPerDay)
                    .then(function (response) {
                        if (response.status == 200) {
                            deferred.resolve();
                        }
                        else {
                            deferred.reject('Error saving max noOfProjects per day');
                        }
                    });

                return deferred.promise;
            },
            logout: function () {
                $http({
                    method: 'POST',
                    url: '/logout'
                })
                .then(function (response) {
                    if (response.status == 200) {
                    window.location.reload();
                    }
                    else {
                        console.log("Logout failed!");
                    }
                });
            }
        };
    }]);