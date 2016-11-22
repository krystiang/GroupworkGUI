


function JSelectionGesture(skeletonId, x, y) {
    
    this.skeletonId = skeletonId;
    
    this.x = x;
    
    this.y = y;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.JSelectionGesture";
}

function JSwipeGesture(skeletonId, direction) {
    
    this.skeletonId = skeletonId;
    
    this.direction = direction;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.JSwipeGesture";
}

function JDragGesture(skeletonId, x, y, phase) {
    
    this.skeletonId = skeletonId;
    
    this.x = x;
    
    this.y = y;
    
    this.phase = phase;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.JDragGesture";
}

function JMoveGesture(skeletonId, x, y) {
    
    this.skeletonId = skeletonId;
    
    this.x = x;
    
    this.y = y;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.JMoveGesture";
}

function JTransformGesture(skeletonId, scale, angle, rotation, phase) {
    
    this.skeletonId = skeletonId;
    
    this.scale = scale;
    
    this.angle = angle;
    
    this.rotation = rotation;
    
    this.phase = phase;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.JTransformGesture";
}

function HandState(id, lx, ly, lz, leftHandState, rx, ry, rz, rightHandState) {
    
    this.id = id;
    
    this.lx = lx;
    
    this.ly = ly;
    
    this.lz = lz;
    
    this.leftHandState = leftHandState;
    
    this.rx = rx;
    
    this.ry = ry;
    
    this.rz = rz;
    
    this.rightHandState = rightHandState;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.HandState";
}

function Event(eventName, skeletonId) {
    
    this.eventName = eventName;
    
    this.skeletonId = skeletonId;
    
    this.tpe = "de.hawhamburg.csti.groupwork.api.Event";
}




var GestureDeserializer = new function() {
	var transform = function(data, deser) {
    	var re = {};
    	Object.keys(data).forEach(function(k) {
    	    var value = data[k];

			if (Array.isArray(value)) {
            	value = data[k].map(function(val) {
                	console.log(val);
                	if(typeof val === 'object')
                    	return deser.deserialize(val);
                	else return val;
            	});
        	} else if(typeof value === 'object') {
    	        value = deser.deserialize(value);
   		    }
        	re[k] = {
            	writable: false,
            	configurable: false,
            	value: value
        	};
    	});
    	return re;
	}

    this.deserialize = function(json) {
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.JSelectionGesture")
            return this.deserializeJSelectionGesture(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.JSwipeGesture")
            return this.deserializeJSwipeGesture(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.JDragGesture")
            return this.deserializeJDragGesture(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.JMoveGesture")
            return this.deserializeJMoveGesture(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.JTransformGesture")
            return this.deserializeJTransformGesture(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.HandState")
            return this.deserializeHandState(json);
        
        if(json.tpe === "de.hawhamburg.csti.groupwork.api.Event")
            return this.deserializeEvent(json);
        
    };

    
    this.deserializeJSelectionGesture = function (data) {
        return Object.create(JSelectionGesture.prototype, transform(data, this));
    };
    
    this.deserializeJSwipeGesture = function (data) {
        return Object.create(JSwipeGesture.prototype, transform(data, this));
    };
    
    this.deserializeJDragGesture = function (data) {
        return Object.create(JDragGesture.prototype, transform(data, this));
    };
    
    this.deserializeJMoveGesture = function (data) {
        return Object.create(JMoveGesture.prototype, transform(data, this));
    };
    
    this.deserializeJTransformGesture = function (data) {
        return Object.create(JTransformGesture.prototype, transform(data, this));
    };
    
    this.deserializeHandState = function (data) {
        return Object.create(HandState.prototype, transform(data, this));
    };
    
    this.deserializeEvent = function (data) {
        return Object.create(Event.prototype, transform(data, this));
    };
    
};