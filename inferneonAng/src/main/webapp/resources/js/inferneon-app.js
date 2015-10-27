angular.module('inferneonApp', ['editableTableWidgets', 'frontendServices', 'spring-security-csrf-token-interceptor'])
    .filter('excludeDeleted', function () {
        return function (input) {
            return _.filter(input, function (item) {
                return item.deleted == undefined || !item.deleted;
            });
        }
    })
    .controller('InferneonCtrl', ['$scope' , 'ProjectService', 'UserService', '$timeout',
        function ($scope, ProjectService, UserService, $timeout) {

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

            updateUserInfo();
            loadProjectData(null, null, null, null, 1);
            loadProjectsList();
            
            function loadProjectsList() {
            	clearMessages();
            	$scope.vm.projectsList.push({description: "Project 1"});
            	$scope.vm.projectsList.push({description: "Project 2"});
            	$scope.vm.projectsList.push({description: "Project 3"});
            	$scope.vm.projectsList.push({description: "Project 4"});
            	$scope.vm.projectsList.push({description: "Project 5"});
            	$scope.vm.projectsList.push({description: "Project 6"});
            	$scope.vm.projectsList.push({description: "Project 7"});
            	
            }


            function showErrorMessage(errorMessage) {
                clearMessages();
                $scope.vm.errorMessages.push({description: errorMessage});
            }

            function updateUserInfo() {
                UserService.getUserInfo()
                    .then(function (userInfo) {
                        $scope.vm.userName = userInfo.userName;
                        $scope.vm.maxNoOfProjectsPerDay = userInfo.maxNoOfProjectsPerDay;
                        $scope.vm.todaysNoOfProjects = userInfo.todaysNoOfProjects ? userInfo.todaysNoOfProjects : 'None';
                        updateNoOfProjectsCounterStatus();
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                    });
            }

            function markAppAsInitialized() {
                if ($scope.vm.appReady == undefined) {
                    $scope.vm.appReady = true;
                }
            }

            function loadProjectData(fromDate, fromTime, toDate, toTime, pageNumber) {
                ProjectService.searchProjects(fromDate, fromTime, toDate, toTime, pageNumber)
                    .then(function (data) {

                        $scope.vm.errorMessages = [];
                        $scope.vm.currentPage = data.currentPage;
                        $scope.vm.totalPages = data.totalPages;

                        $scope.vm.originalProjects = _.map(data.projects, function (project) {
                            project.datetime = project.date + ' ' + project.time;
                            return project;
                        });

                        $scope.vm.projects = _.cloneDeep($scope.vm.originalProjects);

                        _.each($scope.vm.projects, function (project) {
                            project.selected = false;
                        });

                        markAppAsInitialized();

                        if ($scope.vm.projects && $scope.vm.projects.length == 0) {
                            showInfoMessage("No results found.");
                        }
                    },
                    function (errorMessage) {
                        showErrorMessage(errorMessage);
                        markAppAsInitialized();
                    });
            }

            function clearMessages() {
                $scope.vm.errorMessages = [];
                $scope.vm.infoMessages = [];
                $scope.vm.projectsList = [];
            }

            function updateNoOfProjectsCounterStatus() {
                var isNoOfProjectsOK = $scope.vm.todaysNoOfProjects == 'None' || ($scope.vm.todaysNoOfProjects <= $scope.vm.maxNoOfProjectsPerDay);
                $scope.vm.noOfProjectsStatusStyle = isNoOfProjectsOK ? 'cal-limit-ok' : 'cal-limit-nok';
            }

            function showInfoMessage(infoMessage) {
                $scope.vm.infoMessages = [];
                $scope.vm.infoMessages.push({description: infoMessage});
                $timeout(function () {
                    $scope.vm.infoMessages = [];
                }, 1000);
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
                            showErrorMessage(errorMessage);
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

                if ($scope.vm.fromDate && !$scope.vm.toDate || !$scope.vm.fromDate && $scope.vm.toDate) {
                    showErrorMessage("Both from and to dates are needed");
                    errorsFound = true;
                    return;
                }

                if (fromDate > toDate) {
                    showErrorMessage("From date cannot be larger than to date");
                    errorsFound = true;
                }

                if (fromDate.getTime() == toDate.getTime() && $scope.vm.fromTime &&
                    $scope.vm.toTime && $scope.vm.fromTime > $scope.vm.toTime) {
                    showErrorMessage("Inside same day, from time cannot be larger than to time");
                    errorsFound = true;
                }

                if (!errorsFound) {
                    loadProjectData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, page == undefined ? 1 : page);
                }

            };

            $scope.previous = function () {
                if ($scope.vm.currentPage > 1) {
                    $scope.vm.currentPage-= 1;
                    loadProjectData($scope.vm.fromDate, $scope.vm.fromTime,
                        $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
                }
            };

            $scope.next = function () {
                if ($scope.vm.currentPage < $scope.vm.totalPages) {
                    $scope.vm.currentPage += 1;
                    loadProjectData($scope.vm.fromDate, $scope.vm.fromTime,
                        $scope.vm.toDate, $scope.vm.toTime, $scope.vm.currentPage);
                }
            };

            $scope.goToPage = function (pageNumber) {
                if (pageNumber > 0 && pageNumber <= $scope.vm.totalPages) {
                    $scope.vm.currentPage = pageNumber;
                    loadProjectData($scope.vm.fromDate, $scope.vm.fromTime, $scope.vm.toDate, $scope.vm.toTime, pageNumber);
                }
            };

            $scope.add = function () {
                $scope.vm.projects.unshift({
                    id: null,
                    datetime: null,
                    description: null,
                    noOfProjects: null,
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
                        clearMessages();
                        showInfoMessage("deletion successful.");

                        _.remove($scope.vm.projects, function(project) {
                            return project.selected;
                        });

                        $scope.selectionChanged();
                        updateUserInfo();

                    },
                    function () {
                        clearMessages();
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
                    .each(function (project) {
                        if (project.datetime) {
                            var dt = project.datetime.split(" ");
                            project.date = dt[0];
                            project.time = dt[1];
                        }
                    })
                    .map(function (project) {
                        return {
                            id: project.id,
                            date: project.date,
                            time: project.time,
                            description: project.description,
                            noOfProjects: project.noOfProjects,
                            version: project.version
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

                    return originalProject && ( originalProject.date != project.date ||
                        originalProject.time != project.time || originalProject.description != project.description ||
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
                        showErrorMessage(errorMessage);
                    });

            };

            $scope.logout = function () {
                UserService.logout();
            }


        }]);

