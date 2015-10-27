

angular.module('frontendServices', [])
    .service('ProjectService', ['$http', '$q', function($http, $q) {
        return {
            searchProjects: function(fromDate, fromTime, toDate, toTime, pageNumber) {
                var deferred = $q.defer();

                function prepareTime(time) {
                    return time ? '1970/01/01 ' + time : null;
                }

                $http.get('/project/',{
                    params: {
                        fromDate: fromDate,
                        toDate: toDate,
                        fromTime: prepareTime(fromTime),
                        toTime: prepareTime(toTime),
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

            saveProjects: function(dirtyProjects) {
                var deferred = $q.defer();

                $http({
                    method: 'POST',
                    url: '/project',
                    data: dirtyProjects,
                    headers: {
                        "Content-Type": "application/json",
                        "Accept": "text/plain, application/json"
                    }
                })
                .then(function (response) {
                    if (response.status == 200) {
                        deferred.resolve();
                    }
                    else {
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