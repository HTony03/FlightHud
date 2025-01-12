package net.torocraft.flighthud.components;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.torocraft.flighthud.Dimensions;
import net.torocraft.flighthud.FlightComputer;
import net.torocraft.flighthud.HudComponent;

public class FlightPathIndicator extends HudComponent {

  private final Dimensions dim;
  private final FlightComputer computer;

  public FlightPathIndicator(FlightComputer computer, Dimensions dim) {
    this.computer = computer;
    this.dim = dim;
  }

  @Override
  public void render(PoseStack m, float partial, Minecraft client) {
    if (!CONFIG.flightPath_show) {
      return;
    }

    float deltaPitch = computer.pitch - computer.flightPitch;
    float deltaHeading = wrapHeading(computer.flightHeading) - wrapHeading(computer.heading);
    
    if (deltaHeading < -180) {
      deltaHeading += 360;
    }

    float y = dim.yMid;
    float x = dim.xMid;

    y += i(deltaPitch * dim.degreesPerPixel);
    x += i(deltaHeading * dim.degreesPerPixel);

    if (y < dim.tFrame || y > dim.bFrame || x < dim.lFrame || x > dim.rFrame) {
      return;
    }

    float l = x - 3;
    float r = x + 3;
    float t = y - 3;
    float b = y + 3;

    drawVerticalLine(m, l, t, b);
    drawVerticalLine(m, r, t, b);

    drawHorizontalLine(m, l, r, t);
    drawHorizontalLine(m, l, r, b);

    drawVerticalLine(m, x, t - 5, t);
    drawHorizontalLine(m, l - 4, l, y);
    drawHorizontalLine(m, r, r + 4, y);
  }
}