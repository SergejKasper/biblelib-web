(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .factory('HasBookSearch', HasBookSearch);

    HasBookSearch.$inject = ['$resource'];

    function HasBookSearch($resource) {
        var resourceUrl =  'api/_search/has-books/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
