(function() {
    'use strict';

    angular
        .module('bibelBibliothekApp')
        .factory('BorrowerSearch', BorrowerSearch);

    BorrowerSearch.$inject = ['$resource'];

    function BorrowerSearch($resource) {
        var resourceUrl =  'api/_search/borrowers/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
