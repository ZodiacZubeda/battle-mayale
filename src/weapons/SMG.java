package weapons;

import entities.Player;

public class SMG extends Weapon
{
    public SMG(Player p)
    {
        super(6, 30, 1, 7,p);
    }

    @Override
    public String getName() {
        return "SMG";
    }
}
