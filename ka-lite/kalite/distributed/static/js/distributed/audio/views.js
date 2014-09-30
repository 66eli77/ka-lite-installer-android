window.AudioPlayerView = Backbone.View.extend({

    template: HB.template("audio/audio-player"),

    initialize: function() {

        _.bindAll(this);

        var self = this;

        this.possible_points = ds.distributed.points_per_audio || 750;

        this.REQUIRED_PERCENT_FOR_FULL_POINTS = 0.95;

        window.statusModel.loaded.then(function() {
            // load the info about the content itself
            self.data_model = new ContentDataModel({id: self.options.id});
            if (self.data_model.get("id")) {
                self.data_model.fetch().then(function() {

                    if (window.statusModel.get("is_logged_in")) {

                        self.log_collection = new ContentLogCollection([], {content_model: self.data_model});
                        self.log_collection.fetch().then(self.user_data_loaded);

                    }
                });
            }

        });

    },

    render: function() {

        this.$el.html(this.template(this.data_model.attributes));

        this.audio_object = audiojs.create(this.$("audio"))[0];

        this.starting_points = this.log_model.get("points");

        if ((this.log_model.get("last_percent") || 0) > 0) {
            this.audio_object.skipTo(this.log_model.get("last_percent"));
        }

        this.log_model.set("views", this.log_model.get("views") + 1);

        this.initialize_listeners();

        this.points_view = new AudioPlayerPointsView({
            model: this.log_model
        });

        this.points_view.render();

        this.$(".audio-points-wrapper").append(this.points_view.el);
    },

    initialize_listeners: function() {

        var self = this;

        $(this.audio_object.wrapper).on("timeupdate", self.update_progress);
        $(this.audio_object.wrapper).on("play", self.set_last_time);

    },

    update_progress: function(event) {
        var time_now = new Date().getTime();
        // In case of skipping around, only give credit for time actually passed.
        // In case of skipping backwards, make sure time_watched is always non-negative.
        var percent = event.originalEvent.percent;
        var time_engaged = Math.max(0, Math.min(time_now - this.last_time, (percent - this.log_model.get("last_percent"))*this.audio_object.duration));
        time_engaged = isNaN(time_engaged) ? 0 : time_engaged;
        this.log_model.set("last_percent", percent);
        this.last_time = time_now;
        var total_time = this.log_model.get("time_spent") + time_engaged;
        if (total_time/this.audio_object.duration > this.REQUIRED_PERCENT_FOR_FULL_POINTS) {
            var already_complete = this.log_model.get("complete");
            this.log_model.set({
                time_spent: Math.max(this.audio_object.duration, total_time),
                points: this.possible_points,
                progress: 1,
                complete: true,
                completion_counter: (this.log_model.get("completion_counter") || 0) + 1
            });
            if (!already_complete) {
                this.log_model.set({
                    completion_timestamp: window.statusModel.get_server_time()
                });
            }
        } else {
            this.log_model.set({
                time_spent: total_time,
                points: Math.floor(this.possible_points * total_time/this.audio_object.duration),
                progress: total_time/this.audio_object.duration
            });
        }
        this.log_model.save();

        statusModel.set("newpoints", this.log_model.get("points") - this.starting_points);

    },

    set_last_time: function() {
        this.last_time = new Date().getTime();
    },

    user_data_loaded: function() {
        this.log_model = this.log_collection.get_first_log_or_new_log();

        this.render();
    },

    close: function() {
        this.remove();
    }

});

window.AudioPlayerPointsView = Backbone.View.extend({

    initialize: function() {
        this.listenTo(this.model, "change", this.render);
    },

    render: function() {
        this.$el.html(Number(this.model.get("points")));
    }
});