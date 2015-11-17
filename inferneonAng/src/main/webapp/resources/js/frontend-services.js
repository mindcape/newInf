angular.module('frontendServices', ['commonServices','ngResource'])
    .service('ProjectService', ['$http', '$q',  'MessageService',  '$resource', function($http, $q, $resource, MessageService) {
        return {
        	/**
        	 * Load project service or Search project Service
        	 */
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
            
            /**
        	 * Load project service or Search project Service
        	 */
            loadProject: function(projectId) {
                var deferred = $q.defer();
                $http.get('/project/loadProjectById/',{
                    params: {
                        projectId: projectId
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
            /**
             * Delete project Service 
             * This is future purpose added this function in services
             */
            deleteProjects: function(deletedProjectIds) {
                var deferred = $q.defer();

                $http({
                    method: 'DELETE',
                    url: '/project',
                    data: deletedProjectIds,
                    headers: {
                    	"Content-Type": "application/json",
                        "Accept": "text/plain"
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
            /**
             * Save Project Service
             */
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
        	// Get User Info Service
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
            // Logout Service 
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
    }])
	