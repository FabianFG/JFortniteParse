package me.fungames.jfortniteparse.ue4.assets.exports;

import kotlin.Lazy;

import java.util.List;

public class USimpleConstructionScript extends UObject {
    public List<Lazy<USCS_Node>> RootNodes;
    public List<Lazy<USCS_Node>> AllNodes;
    public Lazy<USCS_Node> DefaultSceneRootNode;
}
