// Init angular
var SmartHome = {};

SmartHome.config = {
};

SmartHome.angular = angular.module('SmartHome', []);

SmartHome.fw7 = {
  app : new Framework7({
    animateNavBackIcon: true
  }),
  options : {
    dynamicNavbar: true,
    domCache: true
  },
  views : []
};