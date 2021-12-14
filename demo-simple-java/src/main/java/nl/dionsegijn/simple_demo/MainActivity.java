package nl.dionsegijn.simple_demo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.concurrent.TimeUnit;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti_core.NewEmitter.Emitter;
import nl.dionsegijn.konfetti_core.NewEmitter.EmitterConfig;
import nl.dionsegijn.konfetti_core.Party;
import nl.dionsegijn.konfetti_core.PartyFactory;
import nl.dionsegijn.konfetti_core.Position;
import nl.dionsegijn.konfetti_core.models.Shape;
import nl.dionsegijn.konfetti_core.models.Size;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_heart);
        final Shape.DrawableShape drawableShape = new Shape.DrawableShape(drawable, true);

        final KonfettiView konfettiView = findViewById(R.id.konfettiView);
        EmitterConfig emitterConfig = new Emitter(5L, TimeUnit.SECONDS).perSecond(50);
        Party party = new PartyFactory(emitterConfig)
                .angle(359)
                .spread(90)
                .setStartVelocity(1f, 5f)
                .timeToLive(2000L)
                .shapes(new Shape.Rectangle(0.2f), drawableShape)
                .sizes(new Size(12, 5f))
                .position(new Position.relative(0.0, 0.0).between(new Position.relative(1.0, 0.0)))
                .build();
        konfettiView.setOnClickListener(view ->
                konfettiView.start(party)
        );
    }
}
