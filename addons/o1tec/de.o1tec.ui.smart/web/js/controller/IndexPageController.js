SmartHome.angular.controller('IndexPageController', [ '$scope', '$http', 'InitService', 'DataService',
		function($scope, $http, InitService, DataService) {
			'use strict';

			console.log("IndexPageController called !")
			
			InitService.addEventListener('ready', function() {

				var sitemapName = 'main';
				var pageId = 'main';
			
				DataService.getSitemap(sitemapName, pageId).then(function(result) {
					console.log(result);
					$scope.sitemap = result.data;
				}, function(err) {
					console.error('Error getting sitemap');
					console.error(err);
				});
				
				

			});
			
			
			
			
			

		} ]);