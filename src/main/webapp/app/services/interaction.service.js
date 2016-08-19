(function() {
    'use strict';
    /* globals SockJS, Stomp */

    angular
        .module('bibelBibliothekApp')
        .factory('InteractionService', InteractionService);

    InteractionService.$inject = ['$rootScope', '$window', '$cookies', '$http', '$q', '$state'];

    function InteractionService ($rootScope, $window, $cookies, $http, $q, $state) {
        var stompClient = null;
        var subscriber = null;
        var listener = $q.defer();
        var connected = $q.defer();
        var alreadyConnectedOnce = false;

        var service = {
            connect: connect,
            disconnect: disconnect,
            receive: receive,
            subscribe: subscribe,
            unsubscribe: unsubscribe
        };

        connect();

        return service;

        function connect () {
            //building absolute path so that websocket doesnt fail when deploying with a context path
            var loc = $window.location;
            var url = '//' + loc.host + loc.pathname + 'websocket/tracker';
            var socket = new SockJS(url);
            stompClient = Stomp.over(socket);
            var stateChangeStart;
            var headers = {};
            headers['X-CSRF-TOKEN'] = $cookies[$http.defaults.xsrfCookieName];
            stompClient.connect(headers, function() {
                connected.resolve('success');
                subscribe();
            });
            listener.promise.then(null, null, function(isbnEvent) {
                setTimeout(function(){
                    $state.go('book.new',{isbn
                        : isbnEvent.isbn});
                }, 2000);
            });
            $rootScope.$on('$destroy', function () {
                if(angular.isDefined(stateChangeStart) && stateChangeStart !== null){
                    stateChangeStart();
                }
            });
        }

        function receive () {
            return listener.promise;
        }

        function disconnect () {
            if (stompClient !== null) {
                stompClient.disconnect();
                stompClient = null;
            }
        }
        function subscribe () {
            connected.promise.then(function() {
                subscriber = stompClient.subscribe('/topic/isbn', function(data) {
                    listener.notify(angular.fromJson(data.body));
                });
            }, null, null);
        }

        function unsubscribe () {
            if (subscriber !== null) {
                subscriber.unsubscribe();
            }
            listener = $q.defer();
        }
    }
})();
