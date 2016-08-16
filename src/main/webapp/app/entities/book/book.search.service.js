(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .factory('BookSearch', BookSearch);

    BookSearch.$inject = ['$resource'];

    function BookSearch($resource) {
        var resourceUrl =  'api/_search/books/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
