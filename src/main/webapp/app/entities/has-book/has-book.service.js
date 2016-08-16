(function() {
    'use strict';
    angular
        .module('bibelBibliothekApp')
        .factory('HasBook', HasBook);

    HasBook.$inject = ['$resource', 'DateUtils'];

    function HasBook ($resource, DateUtils) {
        var resourceUrl =  'api/has-books/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.borrowDate = DateUtils.convertLocalDateFromServer(data.borrowDate);
                        data.returnDate = DateUtils.convertLocalDateFromServer(data.returnDate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.borrowDate = DateUtils.convertLocalDateToServer(data.borrowDate);
                    data.returnDate = DateUtils.convertLocalDateToServer(data.returnDate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.borrowDate = DateUtils.convertLocalDateToServer(data.borrowDate);
                    data.returnDate = DateUtils.convertLocalDateToServer(data.returnDate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
