angular.module('app.routes', [])

.config(function($stateProvider, $urlRouterProvider) {

	// Ionic uses AngularUI Router which uses the concept of states
	// Learn more here: https://github.com/angular-ui/ui-router
	// Set up the various states which the app can be in.
	// Each state's controller can be found in controllers.js
	$stateProvider

	.state('tabsController.startpage', {
		url : '/start',
		views : {
			'tab-home' : {
				templateUrl : 'templates/homepage.html',
				controller : 'homepageCtrl'
			}
		}
	})

	.state('tabsController.sitemap', {
		url : '/sitemap',
		views : {
			'tab-sitemap' : {
				templateUrl : 'templates/sitemap.html',
				controller : 'sitemapCtrl'
			}
		}
	})

	.state('tabsController.multiroom', {
		url : '/multiroom',
		views : {
			'tab-multiroom' : {
				templateUrl : 'templates/multiroom.html',
				controller : 'multiroomCtrl'
			}
		}
	})

	.state('tabsController', {
		url : '/tab',
		abstract : true,
		templateUrl : 'templates/tabsController.html'
	})

	;

	// if none of the above states are matched, use this as the fallback
	$urlRouterProvider.otherwise('/tab/sitemap');

});