#!/bin/sh
if curl angular | grep -q "Software Component Management"; then
    echo "Test passed! :-)"
    exit 0
else
    echo "Test failed! :-("
    exit 1
fi
