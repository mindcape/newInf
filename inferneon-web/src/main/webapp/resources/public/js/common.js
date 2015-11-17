var scotchApp = angular.module('common', ['ngMessages'])
    .controller('BaseFormCtrl', ['$scope', '$http', function ($scope, $http) {

        var fieldWithFocus;

        $scope.vm = {
            submitted: false,
            errorMessages: []
        };

        $scope.focus = function (fieldName) {
            fieldWithFocus = fieldName;
        };

        $scope.blur = function (fieldName) {
            fieldWithFocus = undefined;
        };

        $scope.isMessagesVisible = function (fieldName) {
            return fieldWithFocus === fieldName || $scope.vm.submitted;
        };

        $scope.preparePostData = function () {
            var username = $scope.vm.username != undefined ? $scope.vm.username : '';
            var password = $scope.vm.password != undefined ? $scope.vm.password : '';
            var email = $scope.vm.email != undefined ? $scope.vm.email : '';

            return 'username=' + username + '&password=' + password + '&email=' + email;
        }

        $scope.login = function (username, password) {
            var postData = $scope.preparePostData();

            $http({
                method: 'POST',
                url: '/authenticate',
                data: postData,
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Login-Ajax-call": 'true'
                }
            })
            .then(function(response) {
                if (response.data == 'ok') {
                	
                    window.location.replace('/resources/index.html');
                	/*scotchApp.config(function($routeProvider) {
                		$routeProvider

                			// route for the home page
                			.when('/', {
                				templateUrl : '/resources/index.html',
                				controller  : 'mainController'
                			})

                			// route for the about page
                			.when('/about', {
                				templateUrl : 'pages/about.html',
                				controller  : 'aboutController'
                			})

                			// route for the contact page
                			.when('/contact', {
                				templateUrl : 'pages/contact.html',
                				controller  : 'contactController'
                			});
                	});*/
                }
                else {
                    $scope.vm.errorMessages = [];
                    $scope.vm.errorMessages.push({description: 'Invalid User Name or Passsword'});
                }
            });
        }


    }])
    .directive('checkPasswordsMatch', function () {
        return {
            require: 'ngModel',
            link: function (scope, elm, attrs, ngModel) {
                ngModel.$validators.checkPasswordsMatch = function (modelValue, viewValue) {
                    if (scope.vm && scope.vm.password && viewValue) {
                        return scope.vm.password === viewValue;
                    }
                    return true;
                };
            }
        };
    });

// create the controller and inject Angular's $scope
scotchApp.controller('mainController', function($scope) {
	// create a message to display in our view
	$scope.message = 'Everyone come and see how good I look!';
});

scotchApp.controller('aboutController', function($scope) {
	$scope.message = 'Look! I am an about page.';
});

scotchApp.controller('contactController', function($scope) {
	$scope.message = 'Contact us! JK. This is just a demo.';
});