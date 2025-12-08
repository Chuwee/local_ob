export interface EntityCategory {
    id: number;
    parent_id?: number;
    code?: string;
    description?: string;
    base_category_id?: number[];
}

/*
Used to build the tree model displayed on edit entity categories
    - EntityCategoryTreeModel is a Vm for EntityCategory for structuring parent categories-subcategories relations
    - EntityCategoryFlatNode is the result of the transformer method, necessary for the flat tree
*/
export interface EntityCategoryTreeModel extends EntityCategory {
    subcategories?: EntityCategoryTreeModel[];
}
export interface EntityCategoryFlatNode extends EntityCategoryTreeModel {
    expandable: boolean;
    level: number;
}
