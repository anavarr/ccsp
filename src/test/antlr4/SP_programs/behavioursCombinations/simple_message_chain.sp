client [
    server!data @!"";
    server?result @?"";
    server?result2 @?"";
    server!data1 @!"";
    End
]

|

server [
    client?query @?"";
    client!result @!"";
    client!result2 @!"";
    client?query2 @?"";
    End
]