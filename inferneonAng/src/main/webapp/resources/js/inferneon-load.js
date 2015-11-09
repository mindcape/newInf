///////////////////////////////////////////////////////////////////////////////////////////////////////////
//
// Defines the javascript files that need to be loaded and their dependencies.
//
///////////////////////////////////////////////////////////////////////////////////////////////////////////

require.config({
    paths: {
        'angular': '../bower_components/angular/angular',
        'angularMessages': '../bower_components/angular-messages/angular-messages',
        'angularRoute': '../bower_components/angular-route/angular-route',
        'angularResource': '../bower_components/angular-resource/angular-resource',
        'ui.bootstrap.tpls': '../bower_components/angular-bootstrap/ui-bootstrap-tpls.min',
        'csrfInterceptor': '../bower_components/spring-security-csrf-token-interceptor/dist/spring-security-csrf-token-interceptor.min',
        'lodash': '../bower_components/lodash/dist/lodash',
        'bootstrap': '../bower_components/bootstrap/dist/js/bootstrap',
        'jQuery': "../bower_components/jquery/dist/jquery.min", // needed only by the date time picker
        'editableTableWidgets': '../public/js/editable-table-widgets',
        'frontendServices': 'frontend-services',
        'inferneonApp': "inferneon-app",
         'commonServices' : 'common-services'
    },
    shim: {
        'jQuery': {
            exports: "jQuery"
        },
        'angular': {
            exports: "angular"
        },
        'csrfInterceptor': {
            deps: ['angular']
        },
        
        'angularMessages': {
            deps: ['angular']
        },
        'angularRoute': {
            deps: ['angular']
        },
        'angularResource': {
            deps: ['angular']
        },
        
        'editableTableWidgets': {
            deps: ['angular', 'lodash']
        },
        'commonServices': {
            deps: ['angular', 'lodash', 'csrfInterceptor']
        },
        'frontendServices': {
            deps: ['commonServices', 'angularResource']
        },
        'ui.bootstrap.tpls': {
        	deps: ['angular']
        },
        'projects': {
        	deps: ['angular']
        },
        'inferneonApp': {
            deps: [ 'jQuery', 'lodash', 'angular','angularMessages','angularRoute','angularResource','ui.bootstrap.tpls','editableTableWidgets' , 'frontendServices', 'commonServices']
        }
    }
});

require(['inferneonApp'], function () {

    angular.bootstrap(document.getElementById('inferneonApp'), ['inferneonApp']);

});