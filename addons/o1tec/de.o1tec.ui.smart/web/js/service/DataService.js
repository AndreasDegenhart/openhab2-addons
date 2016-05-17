SmartHome.angular.factory('DataService', ['$document', '$http',  function ($document, $http) {
  'use strict';

  var pub = {};
  
  pub.getSitemap = function(sitemapName, pageId) {
	  
	  sitemapName = sitemapName || 'main'; 
	  
	  return $http.get('http://localhost:8080/rest/sitemaps/' + sitemapName + '/' + pageId)
	  
  }
  
  return pub;
  
}]);