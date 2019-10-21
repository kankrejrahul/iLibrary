var defaultScaleValue = 0.1;
var defaultRotationValue = 0;

var rotationValues = [];
var scaleValues = [];

var allCurrentModels = [];

var oneFingerGestureAllowed = false;

var modelName = "";

/*
    This global callback can be utilized to react on the transition from and to 2 finger gestures; specifically, we
    disallow the drag gesture in this case to ensure an intuitive experience.
*/
AR.context.on2FingerGestureStarted = function() {
    oneFingerGestureAllowed = false;
};



var World = {

    platformAssisstedTrackingSupported: false,
    createOverlaysCalled: false,

    modelPaths: [
        "assets/models/clock.wt3",
        "assets/models/couch.wt3",
        "assets/models/officechair.wt3",
        "assets/models/table.wt3",
        "assets/models/trainer.wt3"
    ],

    requestedModel: -1,
    initialDrag: false,
    lastAddedModel: null,



    init: function initFn() {
        /*
            When you'd like to make use of the SMART feature, make sure to call this function and await the result
            in the AR.hardware.smart.onPlatformAssistedTrackingAvailabilityChanged callback.
         */

        AR.hardware.smart.isPlatformAssistedTrackingSupported();
    },

    createOverlays: function createOverlaysFn() {
        if (World.createOverlaysCalled) {
            return;
        }

        World.createOverlaysCalled = true;

        var crossHairsRedImage = new AR.ImageResource("assets/crosshairs_red.png", {
            onError: World.onError
        });
        this.crossHairsRedDrawable = new AR.ImageDrawable(crossHairsRedImage, 1.0);

        var crossHairsBlueImage = new AR.ImageResource("assets/crosshairs_blue.png", {
            onError: World.onError
        });
        this.crossHairsBlueDrawable = new AR.ImageDrawable(crossHairsBlueImage, 1.0);

        var crossHairsGreenImage = new AR.ImageResource("assets/crosshairs_green.png", {
            onError: World.onError
        });
        this.crossHairsGreenDrawable = new AR.ImageDrawable(crossHairsGreenImage, 1.0);

        this.tracker = new AR.InstantTracker({
            onChangedState: function onChangedStateFn(state) {
                var els = [].slice.apply(document.getElementsByClassName("model-button"));
                if (state === AR.InstantTrackerState.INITIALIZING) {
                    els.forEach(function(element) {
                        element.classList.add("image-button-inactive");
                    });

                    document.getElementById("tracking-start-stop-button").src = "assets/buttons/start.png";
                    document.getElementById("tracking-height-slider-container").style.visibility = "visible";
                } else {
                    if (World.platformAssisstedTrackingSupported) {
                        World.showUserInstructions("Running with platform assisted tracking(ARKit or ARCore).");
                    }

                    els.forEach(function(element) {
                        element.classList.remove("image-button-inactive");
                    });

                    document.getElementById("tracking-start-stop-button").src = "assets/buttons/stop.png";
                    document.getElementById("tracking-height-slider-container").style.visibility = "hidden";
                }
            },
            deviceHeight: 1.0,
            onError: World.onError,
            onChangeStateError: World.onError
        });

        this.instantTrackable = new AR.InstantTrackable(this.tracker, {
            drawables: {
                cam: World.crossHairsBlueDrawable,
                initialization: World.crossHairsRedDrawable
            },
            onTrackingStarted: function onTrackingStartedFn() {
                /* Do something when tracking is started (recognized). */
            },
            onTrackingStopped: function onTrackingStoppedFn() {
                /* Do something when tracking is stopped (lost). */
            },
            onTrackingPlaneClick: function onTrackingPlaneClickFn(xPos, yPos) {
                /* React to a the tracking plane being clicked here. */
            },
            onTrackingPlaneDragBegan: function onTrackingPlaneDragBeganFn(xPos, yPos) {
                oneFingerGestureAllowed = true;
                World.updatePlaneDrag(xPos, yPos);
            },
            onTrackingPlaneDragChanged: function onTrackingPlaneDragChangedFn(xPos, yPos) {
                World.updatePlaneDrag(xPos, yPos);
            },
            onTrackingPlaneDragEnded: function onTrackingPlaneDragEndedFn(xPos, yPos) {
                World.updatePlaneDrag(xPos, yPos);
                World.initialDrag = false;
            },
            onError: World.onError
        });

        setInterval(
            function() {
                if (World.tracker.canStartTracking) {
                    World.instantTrackable.drawables.initialization = [World.crossHairsGreenDrawable];
                } else {
                    World.instantTrackable.drawables.initialization = [World.crossHairsRedDrawable];
                }
            },
            1000
        );

        World.setupEventListeners()
    },

    setModelName: function setModelNameFn(name) {
        this.modelName = name;
//        this.defaultScaleValue = newScale;
        console.log('xyz');
        console.log(name);
        console.log(this.modelName);
    },


/*
    setModelName: function setModelNameFn(name)//,newScale
    {
        this.modelName = name;
//        this.defaultScaleValue = newScale;
        console.log("xyz this.modelName: "+this.modelName);
    },
*/


    setupEventListeners: function setupEventListenersFn() {
        document.getElementById("tracking-model-button-clock").addEventListener('touchstart', function( /*ev*/ ) {
            World.requestedModel = 0;
        }, false);
        document.getElementById("tracking-model-button-couch").addEventListener('touchstart', function( /*ev*/ ) {
            World.requestedModel = 1;
        }, false);
        document.getElementById("tracking-model-button-chair").addEventListener('touchstart', function( /*ev*/ ) {
            World.requestedModel = 2;
        }, false);
        document.getElementById("tracking-model-button-table").addEventListener('touchstart', function( /*ev*/ ) {
            World.requestedModel = 3;
        }, false);
        document.getElementById("tracking-model-button-trainer").addEventListener('touchstart', function( /*ev*/ ) {
            World.requestedModel = 4;
        }, false);
    },

    updatePlaneDrag: function updatePlaneDragFn(xPos, yPos) {
        if (World.requestedModel >= 0) {
            World.addModel(World.requestedModel, xPos, yPos);
            World.requestedModel = -1;
            World.initialDrag = true;
        }

        if (World.initialDrag && oneFingerGestureAllowed) {
            World.lastAddedModel.translate = {
                x: xPos,
                y: yPos
            };
        }
    },

    changeTrackerState: function changeTrackerStateFn() {

        if (this.tracker.state === AR.InstantTrackerState.INITIALIZING) {
            this.tracker.state = AR.InstantTrackerState.TRACKING;
        } else {
            this.tracker.state = AR.InstantTrackerState.INITIALIZING;
        }
    },

    changeTrackingHeight: function changeTrackingHeightFn(height) {
        this.tracker.deviceHeight = parseFloat(height);
    },

    addModel: function addModelFn(pathIndex, xpos, ypos) {
        if (World.isTracking()) {
            
//            console.log("xyz rotationValues.length: "+rotationValues.length);
            
            var modelIndex = rotationValues.length;
            World.addModelValues();

            console.log(this.modelName);
            
//            var modelFileName = document.getElementById('modelFileName').value;
            var pathVale = "https://intinic.com/Intinic_Product/sdk_test/Native_Asset_Bundle/NonARC/Models/"+this.modelName;
            
            console.log(pathVale);

            var model = new AR.Model(pathVale, {
                scale: {
                    x: defaultScaleValue,
                    y: defaultScaleValue,
                    z: defaultScaleValue
                },
                translate: {
                    x: xpos,
                    y: ypos
                },
                /*
                    We recommend only implementing the callbacks actually needed as they will cause calls from
                    native to JavaScript being invoked. Especially for the frequently called changed callbacks this
                    should be avoided. In this sample all callbacks are implemented simply for demonstrative purposes.
                */
                onDragBegan: function( /*x, y*/ ) {
                    oneFingerGestureAllowed = true;
                },
                onDragChanged: function(relativeX, relativeY, intersectionX, intersectionY) {
                    if (oneFingerGestureAllowed) {
                        /*
                            We recommend setting the entire translate property rather than its individual components
                            as the latter would cause several call to native, which can potentially lead to performance
                            issues on older devices. The same applied to the rotate and scale property.
                        */
                        this.translate = {
                            x: intersectionX,
                            y: intersectionY
                        };
                    }
                },
                onDragEnded: function(x, y) {
                    /* React to the drag gesture ending. */
                },
                onRotationBegan: function(angleInDegrees) {
                    /* React to the rotation gesture beginning. */
                },
                onRotationChanged: function(angleInDegrees) {
                    this.rotate.z = rotationValues[modelIndex] - angleInDegrees;
                },
                onRotationEnded: function( /*angleInDegrees*/ ) {
                    rotationValues[modelIndex] = this.rotate.z
                },
                onScaleBegan: function(scale) {
                    /* React to the scale gesture beginning. */
                },
                onScaleChanged: function(scale) {
                    var scaleValue = scaleValues[modelIndex] * scale;
                    this.scale = {
                        x: scaleValue,
                        y: scaleValue,
                        z: scaleValue
                    };
                },
                onScaleEnded: function( /*scale*/ ) {
                    scaleValues[modelIndex] = this.scale.x;
                },
                onError: World.onError
            });

            allCurrentModels.push(model);
            World.lastAddedModel = model;
            this.instantTrackable.drawables.addCamDrawable(model);
        }
    },

    isTracking: function isTrackingFn() {
        return (this.tracker.state === AR.InstantTrackerState.TRACKING);
    },

    addModelValues: function addModelValuesFn() {
        rotationValues.push(defaultRotationValue);
        scaleValues.push(defaultScaleValue);
    },

    resetModels: function resetModelsFn() {
        this.instantTrackable.drawables.removeCamDrawable(allCurrentModels);

        allCurrentModels = [];
        World.resetAllModelValues();
    },

    resetAllModelValues: function resetAllModelValuesFn() {
        rotationValues = [];
        scaleValues = [];
    },

    onError: function onErrorFn(error) {
        alert(error);
    },

    showUserInstructions: function showUserInstructionsFn(message) {
        document.getElementById('loadingMessage').innerHTML = message;
    }
};

AR.hardware.smart.onPlatformAssistedTrackingAvailabilityChanged = function(availability) {
    switch (availability) {
        case AR.hardware.smart.SmartAvailability.INDETERMINATE_QUERY_FAILED:
            /* Query failed for some reason; try again or accept the fact. */
            World.showUserInstructions("Could not determine if platform assisted tracking is supported.<br>" +
                "Running without platform assisted tracking (ARKit or ARCore).");
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.CHECKING_QUERY_ONGOING:
            /* Query currently ongoing; be patient and do nothing or inform the user about the ongoing process. */
            break;
        case AR.hardware.smart.SmartAvailability.UNSUPPORTED:
            /* Not supported, create the scene now without platform assisted tracking enabled. */
            World.showUserInstructions("Running without platform assisted tracking (ARKit or ARCore).");
            World.createOverlays();
            break;
        case AR.hardware.smart.SmartAvailability.SUPPORTED_UPDATE_REQUIRED:
        case AR.hardware.smart.SmartAvailability.SUPPORTED:
            /*
                Supported, create the scene now with platform assisted tracking enabled SUPPORTED_UPDATE_REQUIRED
                may be followed by SUPPORTED, make sure not to create the scene twice (see check in createOverlays).
             */
            World.platformAssisstedTrackingSupported = true;
            World.showUserInstructions("Running with platform assisted tracking(ARKit or ARCore). <br> " +
                "Move your phone around until the crosshair turns green, which is when you can start tracking.");
            World.createOverlays();
            break;
    }
};

World.init();