# GeoCodingAddressSearch

I was asked to build this app for a prospective employer.

https://play.google.com/store/apps/details?id=acgmaps.com.searoth.geocodingaddresssearch

Technologies used:
1. Android MVVM architecture with live data and data binding
2. Room database 
3. RecyclerView for search results and favorites
4. Google Maps GeoCoding API https://github.com/arbitur/Geocoding-Android
5. Unit tests for database, lists, fragment and 

Requested requirements:
1. Android 4 and later
2. Use Google Maps GeoCoding API
3. UI responsive during search
4. Results listed in same order as received
5. If results.size > 1 then add "display all on map"
6. If no results then show no results
7. Selecting an item should present map view with marker, centered
8. Marker should have location name and coordinates
9. Save menu button should appear if marker is NOT in database
10. Delete menu button should appear if marker IS in database
11. Confirmation for delete 
12. Unit tests

Above the calling:
1. Show favorites menu button added
2. Remove all favorites menu button added
3. Insert random favorites menu button added
4. Clicking on the map will hide the search layout and results
5. Clicking the search button will show/hide the search layout
6. Clicking the favorites button will show/hide the favorites layout

### Main View
![Question](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120304.png?raw=true)

### Search Results
![JSON](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120336.png?raw=true)

### Show all on map
![JSON](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120352.png?raw=true)

### Extra menu buttons
![JSON](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120410.png?raw=true)

### Show favorites
![JSON](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120433.png?raw=true)

### Should delete?
![JSON](https://github.com/SeaRoth/GeoCodingAddressSearch/blob/master/device-2018-07-01-120519.png?raw=true)
